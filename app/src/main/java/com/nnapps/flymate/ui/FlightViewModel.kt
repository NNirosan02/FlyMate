package com.nnapps.flymate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nnapps.flymate.data.repository.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FlightUiState {
    data object Idle : FlightUiState
    data object LoadingSummary : FlightUiState
    data class SummariesReady(val summaries: List<FlightSummary>) : FlightUiState
    data object LoadingDetails : FlightUiState
    data class DetailsReady(val details: FlightDetails) : FlightUiState
    data class Error(val message: String) : FlightUiState
}

class FlightViewModel(
    private val repository: FlightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlightUiState>(FlightUiState.Idle)
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    fun onFlightCodeEntered(code: String) {
        viewModelScope.launch {
            _uiState.value = FlightUiState.LoadingSummary
            runCatching {
                repository.quickSearch(code)
            }.onSuccess { result ->
                result.onSuccess { list ->
                    _uiState.value = FlightUiState.SummariesReady(list)
                }.onFailure { error ->
                    _uiState.value = FlightUiState.Error(error.message ?: "Flight not found")
                }
            }.onFailure { throwable ->
                _uiState.value = FlightUiState.Error("Network error: ${throwable.localizedMessage}")
            }
        }
    }

    fun onFlightLegSelected(flightNumber: String, legIndex: Int) {
        viewModelScope.launch {
            _uiState.value = FlightUiState.LoadingDetails
            repository.fetchFullTelemetry(flightNumber, legIndex)
                .onSuccess { details ->
                    _uiState.value = FlightUiState.DetailsReady(details)
                }
                .onFailure { error ->
                    _uiState.value = FlightUiState.Error(error.message ?: "Failed to load telemetry")
                }
        }
    }

    fun onClearSearch() {
        _uiState.value = FlightUiState.Idle
    }

    companion object {
        fun provideFactory(repository: FlightRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FlightViewModel(repository) as T
                }
            }
    }
}