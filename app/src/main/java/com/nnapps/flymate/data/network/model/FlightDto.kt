package com.nnapps.flymate.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val networkJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
    encodeDefaults = true
}

@Serializable
data class AeroAirport(
    @SerialName("iata") val iata: String? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class AeroMovementTime(
    @SerialName("scheduledTime") val scheduledTime: AeroTimeValue? = null,
    @SerialName("revisedTime") val revisedTime: AeroTimeValue? = null,
    @SerialName("actualTime") val actualTime: AeroTimeValue? = null
)

@Serializable
data class AeroTimeValue(
    @SerialName("local") val local: String? = null,
    @SerialName("utc") val utc: String? = null
)

@Serializable
data class AeroFlightMovement(
    @SerialName("airport") val airport: AeroAirport? = null,
    @SerialName("scheduledTime") val scheduledTime: AeroTimeValue? = null,
    @SerialName("revisedTime") val revisedTime: AeroTimeValue? = null,
    @SerialName("actualTime") val actualTime: AeroTimeValue? = null,
    @SerialName("terminal") val terminal: String? = null,
    @SerialName("gate") val gate: String? = null
)

@Serializable
data class AeroAirline(
    @SerialName("name") val name: String? = null,
    @SerialName("iata") val iata: String? = null
)

@Serializable
data class AeroAircraft(
    @SerialName("model") val model: String? = null,
    @SerialName("reg") val reg: String? = null,
    @SerialName("modeS") val modeS: String? = null
)

@Serializable
data class AeroFlightItem(
    @SerialName("number") val number: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("airline") val airline: AeroAirline? = null,
    @SerialName("aircraft") val aircraft: AeroAircraft? = null,
    @SerialName("departure") val departure: AeroFlightMovement? = null,
    @SerialName("arrival") val arrival: AeroFlightMovement? = null
)

@Serializable
data class PlanespottersResponse(
    @SerialName("photos") val photos: List<PlanespotterPhoto>? = null
)

@Serializable
data class PlanespotterPhoto(
    @SerialName("id") val id: String? = null,
    @SerialName("thumbnail_large") val thumbnailLarge: PlanespotterImage? = null
)

@Serializable
data class PlanespotterImage(
    @SerialName("src") val src: String? = null
)