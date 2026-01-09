package com.example.receipttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TripDetailsUiState(
    val trip: Trip = Trip(),
    val receipts: List<Receipt> = emptyList(),
)

class TripDetailsViewModel(
    private val tripId: Int,
    private val repository: TrackerRepository,
) :
    ViewModel() {

    private val isNewTrip = tripId == -1
    private val _userEdits = MutableStateFlow(Trip())
    val uiState: StateFlow<TripDetailsUiState> = createUiStateStream()

    private fun createUiStateStream(): StateFlow<TripDetailsUiState> {
        return if (isNewTrip) {
            _userEdits.map { editedTrip -> TripDetailsUiState(trip = editedTrip) }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TripDetailsUiState()
            )
        } else {
            val tripFromDbStream = repository.getTripStream(tripId).filterNotNull()
            val receiptsFromDbStream = repository.getAllReceiptsForTripStream(tripId)
            combine(
                tripFromDbStream,
                receiptsFromDbStream,
                _userEdits
            ) { tripFromDb, receipts, edits ->
                TripDetailsUiState(
                    trip = tripFromDb.copy(
                        name = edits.name.takeIf { it.isNotBlank() } ?: tripFromDb.name,
                        startDate = edits.startDate.takeIf { it.isNotBlank() }
                            ?: tripFromDb.startDate,
                        endDate = edits.endDate.takeIf { it.isNotBlank() } ?: tripFromDb.endDate,
                    ),
                    receipts = receipts
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TripDetailsUiState()
            )
        }
    }

    fun onNameChange(value: String) {
        _userEdits.update { it.copy(name = value) }
    }

    fun onStartDateChange(value: String) {
        _userEdits.update { it.copy(startDate = value) }
    }

    fun onEndDateChange(value: String) {
        _userEdits.update { it.copy(endDate = value) }
    }

    fun onTotalAmountChange(value: Double) {
        _userEdits.update { it.copy(totalAmount = value) }
    }

    fun saveTrip() {
        viewModelScope.launch {
            val editedTrip = _userEdits.value
            if (isNewTrip) {
                repository.insertTrip(editedTrip)
            } else {
                repository.updateTrip(editedTrip.copy(tripId = this@TripDetailsViewModel.tripId))
            }
        }
    }

    fun deleteTrip() {
        if (isNewTrip) return
        viewModelScope.launch {
            repository.deleteTrip(Trip(tripId = tripId))
        }
    }

    fun addReceipt(tripId: Int, imagePath: String, amount: Double, notes: String) {
        viewModelScope.launch {
            val receipt = Receipt(
                tripId = tripId,
                date = LocalDate.now()
                    .toString(), // TODO: May need to be reformatted, looks to simplistic but seems to work
                imageUri = imagePath,
                amount = amount,
                notes = notes
            )
            repository.insertReceipt(receipt)
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