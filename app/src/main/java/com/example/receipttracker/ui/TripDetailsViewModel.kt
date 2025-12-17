package com.example.receipttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TripDetailsUiState(
    val name: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val totalAmount: String = "", // TODO: String for TextFields but has been defined as Double elsewhere
)


class TripDetailsViewModel(
    private val tripId: Int,
    private val repository: TrackerRepository,
) :
    ViewModel() {

    private val isNewTrip = tripId == -1
    private val _currentTripUiState = MutableStateFlow(TripDetailsUiState())
    val currentTripUiState: StateFlow<TripDetailsUiState> = _currentTripUiState.asStateFlow()

    init {
        if (!isNewTrip) {
            loadTrip(tripId)
        }
    }

    private fun loadTrip(id: Int) {
        viewModelScope.launch {
            val trip = repository.getTripStream(id)
                .filterNotNull()
                .first()
            _currentTripUiState.value = TripDetailsUiState(
                name = trip.name,
                startDate = trip.startDate,
                endDate = trip.endDate,
                totalAmount = trip.totalAmount.toString()
            )

        }
    }

    fun onNameChange(value: String) {
        _currentTripUiState.value =
            _currentTripUiState.value.copy(name = value)
    }

    fun onStartDateChange(value: String) {
        _currentTripUiState.value =
            _currentTripUiState.value.copy(startDate = value)
    }

    fun onEndDateChange(value: String) {
        _currentTripUiState.value =
            _currentTripUiState.value.copy(endDate = value)
    }

    fun onTotalAmountChange(value: String) {
        _currentTripUiState.value =
            _currentTripUiState.value.copy(totalAmount = value)
    }

    fun saveTrip() {
        val ui = _currentTripUiState.value
        val amount = ui.totalAmount.toDoubleOrNull() ?: 0.0

        val trip = Trip(
            tripId = if (isNewTrip) 0 else tripId,  // 0 → Let Room autogenerate
            name = ui.name,
            startDate = ui.startDate,
            endDate = ui.endDate,
            totalAmount = amount
        )

        viewModelScope.launch {
            if (isNewTrip) {
                repository.insertTrip(trip)
            } else {
                repository.updateTrip(trip)
            }
        }
    }

    fun deleteTrip() {
        if (isNewTrip) return

        val tripToDelete = Trip(
            tripId = this.tripId,
            name = _currentTripUiState.value.name,
            startDate = _currentTripUiState.value.startDate,
            endDate = _currentTripUiState.value.endDate,
            totalAmount = _currentTripUiState.value.totalAmount.toDoubleOrNull() ?: 0.0
        )

        viewModelScope.launch {
            repository.deleteTrip(tripToDelete)
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(tripId: Int, repository: TrackerRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TripDetailsViewModel(tripId, repository) as T
                }
            }
    }

}