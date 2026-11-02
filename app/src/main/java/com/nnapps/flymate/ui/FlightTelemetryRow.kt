package com.nnapps.flymate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnapps.flymate.ui.theme.NeonCyan
import com.nnapps.flymate.ui.theme.TextMuted

@Composable
fun FlightTelemetryRow(
    flight: FlightDetails,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Altitude Block
        TelemetryTile(
            label = "Altitude",
            primaryValue = "${String.format("%,d", flight.altitudeMeters)} m",
            secondaryValue = flight.flightLevel,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            modifier = Modifier.weight(1f)
        )

        // Speed Block
        TelemetryTile(
            label = "Speed",
            primaryValue = "${flight.speedMph} mph",
            secondaryValue = "${flight.speedKmh} km/h",
            icon = Icons.Default.Speed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TelemetryTile(
    label: String,
    primaryValue: String,
    secondaryValue: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0F1B2B), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF131D2D), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Metric Text
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label.uppercase(),
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = primaryValue,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = secondaryValue,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}