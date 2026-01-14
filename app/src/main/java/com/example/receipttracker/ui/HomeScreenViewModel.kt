package com.example.receipttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val trips: List<Trip> = emptyList(),
)

class HomeViewModel(
    private val repository: TrackerRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        repository.getAllTripsByDateAscStream()                          // Flow<List<Trip>
            .map { trips -> HomeUiState(trips = trips) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                HomeUiState()
            )

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTripById(trip.tripId)
        }
    }
}

