package com.nnapps.flymate.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nnapps.flymate.R
import com.nnapps.flymate.ui.theme.FlyMateTheme
import com.nnapps.flymate.ui.theme.NeonCyan
import com.nnapps.flymate.ui.theme.RadarGreen
import com.nnapps.flymate.ui.theme.TextMuted

@Composable
fun HeroFlightCard(flight: FlightDetails, modifier: Modifier = Modifier) {
    val cardBg = Color(0xFF0F1B2B)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Image Box with Dark Fade Gradient Scrim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFF131D2D))
            ) {
                if (!flight.aircraftImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = flight.aircraftImageUrl,
                        contentDescription = stringResource(R.string.aircraft_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Vertical scrim gradient fading into the card background color
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    cardBg.copy(alpha = 0.4f),
                                    cardBg.copy(alpha = 0.85f),
                                    cardBg
                                )
                            )
                        )
                )
            }

            // Route Details & Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Origin -> Destination row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = flight.originIata,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = flight.originAirport,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(90f)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = flight.destIata,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = flight.destAirport,
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val progressClamped = flight.progress.coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Full width track background
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        ) {
                            val y = size.height / 2
                            val currentX = size.width * progressClamped

                            // Inactive dark track
                            drawLine(
                                color = Color(0xFF1E2E42),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            // Active neon gradient
                            drawLine(
                                brush = Brush.horizontalGradient(listOf(NeonCyan, RadarGreen)),
                                start = Offset(0f, y),
                                end = Offset(currentX, y),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Airplane head aligned along the exact progress fraction
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressClamped)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(16.dp)
                                    .rotate(90f)
                            )
                        }
                    }

                    // Elapsed, Model, and Remaining text labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.elapsed, flight.elapsedTime),
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${flight.flightNumber}  ${flight.aircraftModel}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.remaining, flight.remainingTime),
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleDetailsSection(flight: FlightDetails, modifier: Modifier = Modifier) {
    val isArrived = flight.arrStatusNote.equals("Arrived", ignoreCase = true)
    val isExpected = flight.arrStatusNote.equals("Expected", ignoreCase = true)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ScheduleCard(
                title = if (isExpected) stringResource(R.string.scheduled_departure) else stringResource(
                    R.string.departed
                ),
                date = flight.depDate,
                time = flight.depTime,
                gate = flight.depGate,
                terminal = flight.depTerminal,
                isArrival = false,
                modifier = Modifier.weight(1f)
            )

            ScheduleCard(
                title = if (isArrived) stringResource(R.string.arrived) else stringResource(R.string.estimated_arrival),
                date = flight.arrDate,
                time = flight.arrTime,
                gate = flight.arrGate,
                terminal = flight.arrTerminal,
                isArrival = true,
                modifier = Modifier.weight(1f)
            )
        }

        val pillBgColor = when {
            isArrived -> Color(0xFF1B2430)
            isExpected -> Color(0xFF0F2537)
            else -> Color(0xFF07241C)
        }

        val pillTextColor = when {
            isArrived -> TextMuted
            isExpected -> NeonCyan
            else -> RadarGreen
        }

        Box(
            modifier = Modifier
                .offset(y = 0.dp)
                .background(pillBgColor, RoundedCornerShape(50.dp))
                .border(1.dp, pillTextColor.copy(alpha = 0.7f), RoundedCornerShape(50.dp))
                .padding(horizontal = 20.dp, vertical = 7.dp)
        ) {
            Text(
                text = flight.arrStatusNote,
                color = pillTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun ScheduleCard(
    title: String,
    date: String,
    time: String,
    gate: String,
    terminal: String,
    isArrival: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B2B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isArrival) Icons.Default.Schedule else Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(15.dp)
                )
                Column {
                    Text(
                        text = title,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = date,
                        color = TextMuted.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = time,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(stringResource(R.string.gate), color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flight,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier
                                .size(13.dp)
                                .rotate(45f)
                        )
                        Text(
                            text = gate,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(stringResource(R.string.terminal), color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = terminal,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapAndTelemetryCard(flight: FlightDetails, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF09121E))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFF03070E))
            ) {
                // Procedural Globe Horizon & Flight Path
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 1. Lower Earth Limb (Gently curved horizon that stays below the flight path)
                    val globePath = Path().apply {
                        moveTo(-w * 0.2f, h * 0.98f)
                        quadraticTo(w * 0.5f, h * 0.58f, w * 1.2f, h * 0.98f)
                        lineTo(w * 1.2f, h)
                        lineTo(-w * 0.2f, h)
                        close()
                    }

                    // Globe deep-ocean fill
                    drawPath(
                        path = globePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF092032), Color(0xFF040A12)),
                            startY = h * 0.58f,
                            endY = h
                        )
                    )

                    // Atmospheric limb glow
                    drawPath(
                        path = Path().apply {
                            moveTo(-w * 0.2f, h * 0.98f)
                            quadraticTo(w * 0.5f, h * 0.58f, w * 1.2f, h * 0.98f)
                        },
                        brush = Brush.verticalGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.45f), Color.Transparent),
                            startY = h * 0.55f,
                            endY = h * 0.75f
                        ),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 2. Flight Path Arc Coordinates
                    val startX = w * 0.16f
                    val startY = h * 0.65f
                    val endX = w * 0.84f
                    val endY = h * 0.54f
                    val controlX = w * 0.48f
                    val controlY = h * 0.14f

                    val arcPath = Path().apply {
                        moveTo(startX, startY)
                        quadraticTo(controlX, controlY, endX, endY)
                    }

                    // Faint shadow track
                    drawPath(
                        path = arcPath,
                        color = Color(0xFF142436),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Active neon trajectory
                    drawPath(
                        path = arcPath,
                        brush = Brush.horizontalGradient(listOf(NeonCyan, RadarGreen)),
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Origin node
                    drawCircle(NeonCyan.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = Offset(startX, startY))
                    drawCircle(Color(0xFF09121E), radius = 4.5.dp.toPx(), center = Offset(startX, startY))
                    drawCircle(NeonCyan, radius = 3.dp.toPx(), center = Offset(startX, startY))

                    // Destination node
                    drawCircle(RadarGreen.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = Offset(endX, endY))
                    drawCircle(Color(0xFF09121E), radius = 4.5.dp.toPx(), center = Offset(endX, endY))
                    drawCircle(RadarGreen, radius = 3.dp.toPx(), center = Offset(endX, endY))
                }

                // Origin Label (ICN) - positioned clearly above/beside the node
                Text(
                    text = flight.originIata,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 82.dp)
                )

                // Destination Label (AMS)
                Text(
                    text = flight.destIata,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 105.dp)
                )

                // Plane rotated along the flight trajectory
                Icon(
                    imageVector = Icons.Default.AirplanemodeActive,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 46.dp)
                        .size(19.dp)
                        .rotate(75f)
                )
            }

            // Bottom Section: Unified Telemetry Dock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A1422))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Altitude
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = stringResource(R.string.altitude).uppercase(),
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "${String.format("%,d", flight.altitudeMeters)} m",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = flight.flightLevel,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(34.dp)
                        .background(Color(0xFF1B2B3D))
                )

                // Speed
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = stringResource(R.string.speed).uppercase(),
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = stringResource(R.string.mph, flight.speedMph),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.km_h, flight.speedKmh),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlightConfirmationPill(
    flight: FlightSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1B2B))
            .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = flight.flightNumber,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = flight.aircraft,
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Text(
                text = "${flight.originIata} → ${flight.destIata} • ${flight.depDate}",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Status badge: LIVE, ARRIVED, or EXPECTED
        when {
            flight.isLive -> {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "✈", fontSize = 10.sp, color = Color.Black)
                    Text(text = "LIVE", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }
            flight.status.equals("Arrived", ignoreCase = true) -> {
                Text(
                    text = stringResource(R.string.arrived).uppercase(),
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFF1B2430), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            else -> {
                Text(
                    text = flight.status.uppercase(),
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(NeonCyan.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E18)
@Composable
fun FlightConfirmationPillPreview() {
    val sampleSummary = FlightSummary(
        flightNumber = "KE925",
        airline = "Korean Air",
        aircraft = "B77W",
        originIata = "ICN",
        destIata = "AMS",
        depDate = "Sat 31 Oct",
        status = "En Route",
        isLive = true
    )
    FlyMateTheme {
        FlightConfirmationPill(
            flight = sampleSummary,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E18)
@Composable
fun HeroFlightCardPreview() {
    val sampleFlight = FlightDetails(
        flightNumber = "KE925",
        airline = "Korean Air",
        aircraftModel = "B77W",
        originIata = "ICN",
        originAirport = "Incheon Intl",
        destIata = "AMS",
        destAirport = "Schiphol",
        depTime = "12:11 (KST)",
        depDate = "Sat 31 Oct",
        depGate = "231",
        depTerminal = "2",
        arrTime = "14:50 (CET)",
        arrDate = "Sat 31 Oct",
        arrGate = "F6",
        arrTerminal = "-",
        arrStatusNote = "Arriving 3h 40m early",
        elapsedTime = "8h 19m",
        remainingTime = "2h 20m",
        progress = 0.78f,
        altitudeMeters = 10350,
        flightLevel = "FL340",
        speedMph = 570,
        speedKmh = 917
    )
    FlyMateTheme {
        HeroFlightCard(flight = sampleFlight)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080E18)
@Composable
fun ScheduleDetailsSectionPreview() {
    val sampleFlight = FlightDetails(
        flightNumber = "KE925",
        airline = "Korean Air",
        aircraftModel = "B77W",
        originIata = "ICN",
        originAirport = "Incheon Intl",
        destIata = "AMS",
        destAirport = "Schiphol",
        depTime = "12:11 (KST)",
        depDate = "Sat 31 Oct",
        depGate = "231",
        depTerminal = "2",
        arrTime = "14:50 (CET)",
        arrDate = "Sat 31 Oct",
        arrGate = "F6",
        arrTerminal = "-",
        arrStatusNote = "Arriving 3h 40m early",
        elapsedTime = "8h 19m",
        remainingTime = "2h 20m",
        progress = 0.78f,
        altitudeMeters = 10350,
        flightLevel = "FL340",
        speedMph = 570,
        speedKmh = 917
    )
    FlyMateTheme {
        ScheduleDetailsSection(flight = sampleFlight)
    }
}
