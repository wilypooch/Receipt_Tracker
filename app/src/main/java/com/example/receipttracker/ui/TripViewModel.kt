package com.example.receipttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

data class TripDetailsUiState(
    val name: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val totalAmount: String = "", // TODO: String for TextFields but has been defined as Double elsewhere
)

class TripViewModel(private val repository: TrackerRepository) : ViewModel() {

    val homeListState: StateFlow<List<Trip>> = repository.getAllTripsByDateAscStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _currentTripUiState = MutableStateFlow(TripDetailsUiState())
    val currentTripUiState: StateFlow<TripDetailsUiState> = _currentTripUiState.asStateFlow()
}