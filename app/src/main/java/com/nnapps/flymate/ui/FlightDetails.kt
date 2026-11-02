package com.nnapps.flymate.ui

data class FlightDetails(
    val flightNumber: String,
    val airline: String,
    val aircraftModel: String,
    val originIata: String,
    val originAirport: String,
    val destIata: String,
    val destAirport: String,
    val depTime: String,
    val depDate: String,
    val depGate: String,
    val depTerminal: String,
    val arrTime: String,
    val arrDate: String,
    val arrGate: String,
    val arrTerminal: String,
    val arrStatusNote: String,
    val elapsedTime: String,
    val remainingTime: String,
    val progress: Float,
    val aircraftImageUrl: String? = null,
    val altitudeMeters: Int,
    val flightLevel: String,
    val speedMph: Int,
    val speedKmh: Int,

)

data class FlightSummary(
    val flightNumber: String,
    val airline: String,
    val aircraft: String,
    val originIata: String,
    val destIata: String,
    val depDate: String,
    val status: String,
    val isLive: Boolean,
    val legIndex: Int = 0
)