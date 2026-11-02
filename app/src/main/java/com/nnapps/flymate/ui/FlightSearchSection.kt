package com.nnapps.flymate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnapps.flymate.ui.theme.CardSurface
import com.nnapps.flymate.ui.theme.NeonCyan
import com.nnapps.flymate.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

enum class SearchMode { FLIGHT_NUMBER, ROUTE }

@Composable
fun FlightSearchSection(
    modifier: Modifier = Modifier,
    onSearchCode: (String) -> Unit = {},
    onSearchRoute: (from: String, to: String) -> Unit = { _, _ -> },
    onClear: () -> Unit = {}
) {
    var selectedMode by remember { mutableStateOf(SearchMode.FLIGHT_NUMBER) }
    var flightCode by remember { mutableStateOf("") }
    var isValidCode by remember { mutableStateOf(false) }
    var originCode by remember { mutableStateOf("ICN") }
    var destCode by remember { mutableStateOf("AMS") }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Debounce for single Flight Code
    LaunchedEffect(flightCode, selectedMode) {
        if (selectedMode == SearchMode.FLIGHT_NUMBER) {
            val clean = flightCode.trim().uppercase()
            val valid = isValidFlightNumber(clean)
            isValidCode = valid

            if (valid) {
                delay(1200.milliseconds)
                onSearchCode(clean)
            } else if (clean.isEmpty()) {
                onClear()
            }
        }
    }

    // Debounce for Route Search (Triggers when both are valid 3-letter IATA codes)
    LaunchedEffect(originCode, destCode, selectedMode) {
        if (selectedMode == SearchMode.ROUTE) {
            val cleanOrigin = originCode.trim().uppercase()
            val cleanDest = destCode.trim().uppercase()

            if (cleanOrigin.length == 3 && cleanDest.length == 3) {
                delay(800.milliseconds)
                onSearchRoute(cleanOrigin, cleanDest)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tab switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131D2D), RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ModeTab(
                title = "Flight Number",
                isSelected = selectedMode == SearchMode.FLIGHT_NUMBER,
                modifier = Modifier.weight(1f)
            ) { selectedMode = SearchMode.FLIGHT_NUMBER }

            ModeTab(
                title = "Route (Airports)",
                isSelected = selectedMode == SearchMode.ROUTE,
                modifier = Modifier.weight(1f)
            ) { selectedMode = SearchMode.ROUTE }
        }

        when (selectedMode) {
            SearchMode.FLIGHT_NUMBER -> {
                OutlinedTextField(
                    value = flightCode,
                    onValueChange = { flightCode = it.uppercase() },
                    placeholder = { Text("e.g. KAL925, BA1388", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            val clean = flightCode.trim().uppercase()
                            if (isValidFlightNumber(clean)) {
                                keyboardController?.hide()
                                onSearchCode(clean)
                            }
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isValidCode) NeonCyan else TextMuted
                        )
                    },
                    trailingIcon = {
                        if (flightCode.isNotEmpty()) {
                            IconButton(onClick = {
                                flightCode = ""
                                onClear()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F1B2B),
                        unfocusedContainerColor = Color(0xFF0F1B2B),
                        focusedBorderColor = if (isValidCode) NeonCyan else Color(0xFF1E2E42),
                        unfocusedBorderColor = if (isValidCode) NeonCyan.copy(alpha = 0.5f) else Color(0xFF1E2E42),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SearchMode.ROUTE -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = originCode,
                        onValueChange = { originCode = it.uppercase().take(3) },
                        label = { Text("Origin (IATA)", color = TextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F1B2B),
                            unfocusedContainerColor = Color(0xFF0F1B2B),
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E2E42),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "to",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )

                    OutlinedTextField(
                        value = destCode,
                        onValueChange = { destCode = it.uppercase().take(3) },
                        label = { Text("Dest (IATA)", color = TextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                val cleanOrigin = originCode.trim().uppercase()
                                val cleanDest = destCode.trim().uppercase()
                                if (cleanOrigin.length == 3 && cleanDest.length == 3) {
                                    keyboardController?.hide()
                                    onSearchRoute(cleanOrigin, cleanDest)
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F1B2B),
                            unfocusedContainerColor = Color(0xFF0F1B2B),
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E2E42),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FlightSearchResultItem(
    flightNumber: String,
    airline: String,
    aircraft: String,
    originIata: String,
    destIata: String,
    isLive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = flightNumber,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = aircraft,
                    color = NeonCyan,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Text(
                text = "$originIata → $destIata",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        if (isLive) {
            Text(
                text = "✈ LIVE",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier
                    .background(Color(0xFFFFD54F), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun ModeTab(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF1F2F46) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

fun isValidFlightNumber(query: String): Boolean {
    val clean = query.trim().uppercase()
    val flightRegex = Regex("^([A-Z]{3}|[A-Z]{2}|[A-Z][0-9]|[0-9][A-Z])\\s?[0-9]{1,4}[A-Z]?$")
    return flightRegex.matches(clean)
}

@Preview(name = "Flight Search - Flight Number Mode", showBackground = true, backgroundColor = 0xFF080E18)
@Composable
fun FlightSearchSectionPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF080E18))
            .padding(vertical = 12.dp)
    ) {
        FlightSearchSection(
            onSearchCode = {},
            onSearchRoute = { _, _ -> }
        )
    }
}