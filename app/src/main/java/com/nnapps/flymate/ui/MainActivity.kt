package com.nnapps.flymate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nnapps.flymate.BuildConfig
import com.nnapps.flymate.data.network.NetworkClient
import com.nnapps.flymate.data.repository.FlightRepositoryImpl
import com.nnapps.flymate.ui.theme.DarkBackground
import com.nnapps.flymate.ui.theme.FlyMateTheme
import com.nnapps.flymate.ui.theme.NeonCyan
import com.nnapps.flymate.ui.theme.RadarGreen
import com.nnapps.flymate.ui.theme.TextMuted

class MainActivity : ComponentActivity() {

    private val apiService by lazy {
        NetworkClient.createFlightApiService(apiKey = BuildConfig.RAPIDAPI_KEY)
    }
    private val repository by lazy {
        FlightRepositoryImpl(apiService)
    }

    private val viewModel: FlightViewModel by viewModels {
        FlightViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlyMateTheme {
                FlyMateApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FlyMateApp(viewModel: FlightViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isConfirmed = uiState is FlightUiState.DetailsReady

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            AnimatedVisibility(
                visible = false,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomActionBar(
                    onNotifyClick = { /* Set notification */ },
                    onSaveClick = { /* Save flight locally */ },
                    onShareClick = { /* Share flight details */ },
                    onMoreClick = { /* Extra options */ }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MainHeaderSection()
            FlightSearchSection(
                onSearchCode = { code ->
                    viewModel.onFlightCodeEntered(code)
                },
                onClear = {
                    viewModel.onClearSearch()
                }
            )

            // Step 1: Confirmation Pill or Loading Shimmer
            when (val state = uiState) {
                is FlightUiState.LoadingSummary -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                is FlightUiState.SummariesReady -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select a flight:",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        state.summaries.forEach { summary ->
                            FlightConfirmationPill(
                                flight = summary,
                                onClick = {
                                    viewModel.onFlightLegSelected(summary.flightNumber, summary.legIndex)
                                }
                            )
                        }
                    }
                }

                is FlightUiState.LoadingDetails -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = RadarGreen,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }

                is FlightUiState.DetailsReady -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HeroFlightCard(flight = state.details)
                        ScheduleDetailsSection(flight = state.details)
//                        MapAndTelemetryCard(flight = state.details)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                is FlightUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .background(Color(0xFF2E1218), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE53935), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = state.message,
                            color = Color(0xFFFF8A80),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                FlightUiState.Idle -> {
                    // Empty state waiting for input
                }
            }
        }
    }
}