package com.nnapps.flymate.data.repository

import com.nnapps.flymate.calculateFlightTimeline
import com.nnapps.flymate.data.network.FlightApiService
import com.nnapps.flymate.data.network.PhotoClient
import com.nnapps.flymate.ui.FlightDetails
import com.nnapps.flymate.ui.FlightSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.Duration
import java.time.OffsetDateTime

fun calculateStatusNote(status: String?, arrRawTime: String?): String {
    val cleanStatus = status?.trim() ?: ""

    // 1. Explicit terminal states
    if (cleanStatus.contains("Arrived", ignoreCase = true)) {
        return "Arrived"
    }
    if (cleanStatus.contains("Expected", ignoreCase = true) || cleanStatus.contains("Scheduled", ignoreCase = true)) {
        return "Expected"
    }

    // 2. Active / EnRoute flight -> Calculate remaining duration
    if (cleanStatus.contains("EnRoute", ignoreCase = true) || cleanStatus.contains("Active", ignoreCase = true)) {
        if (!arrRawTime.isNullOrBlank()) {
            try {
                val cleanedArr = arrRawTime.trim().replace(" ", "T")
                val targetArrTime = OffsetDateTime.parse(cleanedArr)
                val now = OffsetDateTime.now(targetArrTime.offset)

                val diff = Duration.between(now, targetArrTime)
                if (!diff.isNegative) {
                    val hours = diff.toHours()
                    val minutes = diff.toMinutes() % 60
                    return when {
                        hours > 0 -> "Arriving in ${hours}h ${minutes}m"
                        minutes > 0 -> "Arriving in ${minutes}m"
                        else -> "Landing shortly"
                    }
                }
            } catch (e: DateTimeParseException) {
                // If ISO parse fails, fallback
            }
        }
        return "2h 20m Remaining"
    }

    // Default fallback
    return cleanStatus.ifEmpty { "On Time" }
}
fun formatAeroTime(rawTime: String?, fallback: String): String {
    if (rawTime.isNullOrBlank()) return fallback

    return try {
        val cleaned = rawTime.trim().replace(" ", "T")
        val parsed = OffsetDateTime.parse(cleaned)
        parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: DateTimeParseException) {
        val timeRegex = Regex("(\\d{2}:\\d{2})")
        timeRegex.find(rawTime)?.value ?: fallback
    }
}

fun formatAeroDate(rawTime: String?, fallback: String = "Today"): String {
    if (rawTime.isNullOrBlank()) return fallback

    return try {
        val cleaned = rawTime.trim().replace(" ", "T")
        val parsed = OffsetDateTime.parse(cleaned)
        parsed.format(DateTimeFormatter.ofPattern("EEE d MMM"))
    } catch (e: Exception) {
        fallback
    }
}

interface FlightRepository {
    suspend fun quickSearch(flightCode: String): Result<List<FlightSummary>>
    suspend fun fetchFullTelemetry(flightCode: String, selectedLegIndex: Int): Result<FlightDetails>
}

class FlightRepositoryImpl(
    private val apiService: FlightApiService
) : FlightRepository {

    override suspend fun quickSearch(flightCode: String): Result<List<FlightSummary>> =
        withContext(Dispatchers.IO) {
            val cleanCode = flightCode.trim().uppercase()
            try {
                val response = apiService.getFlightByNumber(cleanCode)
                val flightList = response.body()

                if (response.isSuccessful && !flightList.isNullOrEmpty()) {
                    val summaries = flightList.mapIndexed { index, flight ->
                        val depRaw = flight.departure?.actualTime?.local
                            ?: flight.departure?.revisedTime?.local
                            ?: flight.departure?.scheduledTime?.local

                        val statusClean = flight.status ?: "Scheduled"
                        val isLive = statusClean.contains("EnRoute", ignoreCase = true) ||
                                statusClean.contains("Active", ignoreCase = true)

                        FlightSummary(
                            flightNumber = flight.number ?: cleanCode,
                            airline = flight.airline?.name ?: "Airline",
                            aircraft = flight.aircraft?.model ?: "Aircraft",
                            originIata = flight.departure?.airport?.iata ?: "---",
                            destIata = flight.arrival?.airport?.iata ?: "---",
                            depDate = formatAeroDate(depRaw, "Today"),
                            status = statusClean,
                            isLive = isLive,
                            legIndex = index
                        )
                    }
                    Result.success(summaries)
                } else {
                    Result.success(listOf(createMockSummary(cleanCode)))
                }
            } catch (e: Exception) {
                Result.success(listOf(createMockSummary(cleanCode)))
            }
        }

    override suspend fun fetchFullTelemetry(flightCode: String, selectedLegIndex: Int): Result<FlightDetails> =
        withContext(Dispatchers.IO) {
            val cleanCode = flightCode.trim().uppercase()
            try {
                val response = apiService.getFlightByNumber(cleanCode)
                val flightList = response.body()

                if (response.isSuccessful && !flightList.isNullOrEmpty()) {
                    // Pick the explicit leg selected by the user
                    val flight = flightList.getOrElse(selectedLegIndex) { flightList.first() }

                    val depRaw = flight.departure?.actualTime?.local
                        ?: flight.departure?.revisedTime?.local
                        ?: flight.departure?.scheduledTime?.local
                    val arrRaw = flight.arrival?.actualTime?.local
                        ?: flight.arrival?.revisedTime?.local
                        ?: flight.arrival?.scheduledTime?.local

                    val depTimeFormatted = formatAeroTime(depRaw, "--:--")
                    val arrTimeFormatted = formatAeroTime(arrRaw, "--:--")
                    val timeline = calculateFlightTimeline(depRaw, arrRaw)
                    val statusNote = calculateStatusNote(flight.status, arrRaw)

                    val tailNumber = flight.aircraft?.reg
                    val photoUrl = if (!tailNumber.isNullOrBlank()) {
                        try {
                            val photoResp = PhotoClient.service.getPhotoByReg(tailNumber)
                            photoResp.body()?.photos?.firstOrNull()?.thumbnailLarge?.src
                        } catch (e: Exception) {
                            null
                        }
                    } else null

                    Result.success(
                        FlightDetails(
                            flightNumber = flight.number ?: cleanCode,
                            airline = flight.airline?.name ?: "Airline",
                            aircraftModel = flight.aircraft?.model ?: "Aircraft",
                            aircraftImageUrl = photoUrl,
                            originIata = flight.departure?.airport?.iata ?: "---",
                            originAirport = flight.departure?.airport?.name ?: "Origin Airport",
                            destIata = flight.arrival?.airport?.iata ?: "---",
                            destAirport = flight.arrival?.airport?.name ?: "Destination Airport",
                            depTime = depTimeFormatted,
                            depDate = formatAeroDate(depRaw, "Today"),
                            depGate = flight.departure?.gate ?: "--",
                            depTerminal = flight.departure?.terminal ?: "--",
                            arrTime = arrTimeFormatted,
                            arrDate = formatAeroDate(arrRaw, "Today"),
                            arrGate = flight.arrival?.gate ?: "--",
                            arrTerminal = flight.arrival?.terminal ?: "--",
                            arrStatusNote = statusNote,
                            elapsedTime = timeline.elapsedTime,
                            remainingTime = timeline.remainingTime,
                            progress = timeline.progress,
                            altitudeMeters = 10350,
                            flightLevel = "FL340",
                            speedMph = 570,
                            speedKmh = 917
                        )
                    )
                } else {
                    Result.success(createMockDetails(cleanCode))
                }
            } catch (e: Exception) {
                Result.success(createMockDetails(cleanCode))
            }
        }
    private fun createMockSummary(code: String): FlightSummary {
        return FlightSummary(
            flightNumber = code,
            airline = "British Airways",
            aircraft = "Airbus A350-1000",
            originIata = "LHR",
            destIata = "GRU",
            depDate = "Sat 31 Oct",
            status = "Expected",
            isLive = false,
            legIndex = 0
        )
    }

    private fun createMockDetails(code: String): FlightDetails {
        val isKorean = code.startsWith("KE") || code.startsWith("KAL")
        return FlightDetails(
            flightNumber = code,
            airline = if (isKorean) "Korean Air" else "British Airways",
            aircraftModel = if (isKorean) "B77W" else "A35K",
            originIata = if (isKorean) "ICN" else "LHR",
            originAirport = if (isKorean) "Incheon Intl" else "Heathrow",
            destIata = if (isKorean) "AMS" else "JFK",
            destAirport = if (isKorean) "Schiphol" else "John F. Kennedy",
            depTime = if (isKorean) "12:11 (KST)" else "09:45 (GMT)",
            depDate = "Sat 31 Oct",
            depGate = if (isKorean) "231" else "B32",
            depTerminal = if (isKorean) "2" else "5",
            arrTime = if (isKorean) "14:50 (CET)" else "12:30 (EDT)",
            arrDate = "Sat 31 Oct",
            arrGate = if (isKorean) "F6" else "4",
            arrTerminal = if (isKorean) "-" else "7",
            arrStatusNote = "Arriving 3h 40m early",
            elapsedTime = "8h 19m",
            remainingTime = "2h 20m",
            progress = 0.78f,
            altitudeMeters = 10350,
            flightLevel = "FL340",
            speedMph = 570,
            speedKmh = 917
        )
    }
}