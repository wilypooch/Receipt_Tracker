package com.example.receipttracker.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TripDetailsUiState(
    val trip: Trip = Trip(),
    val receipts: List<Receipt> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailsViewModel(
    initialTripId: Int,
    private val repository: TrackerRepository,
) :
    ViewModel() {

    private val _currentTripId = MutableStateFlow(initialTripId)
    private val _userEdits = MutableStateFlow(Trip())
    val uiState: StateFlow<TripDetailsUiState> =
        _currentTripId.filter { it != -1 }.flatMapLatest { id ->
            val tripFromDbStream = repository.getTripStream(id).filterNotNull()
            val receiptsFromDbStream = repository.getAllReceiptsForTripStream(id)

            combine(tripFromDbStream, receiptsFromDbStream, _userEdits) { tripDb, receipts, edits ->
                val calculatedTotal = receipts.sumOf { it.amount }
                TripDetailsUiState(
                    trip = tripDb.copy(
                        name = edits.name.ifBlank { tripDb.name },
                        startDate = edits.startDate.ifBlank { tripDb.startDate },
                        endDate = edits.endDate.ifBlank { tripDb.endDate },
                        totalAmount = calculatedTotal
                    ), receipts = receipts
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TripDetailsUiState()
        )

    init {
        if (initialTripId == -1) {
            viewModelScope.launch {
                val newId: Long = repository.insertTrip(Trip())
                _currentTripId.value = newId.toInt()
            }
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

    fun saveTrip() {
        viewModelScope.launch {
            val currentTrip = uiState.value.trip
            repository.updateTrip(currentTrip)
        }
    }

    fun deleteTrip() {
        viewModelScope.launch {
            val allReceipts = repository.getReceiptsForTripForDeletion(_currentTripId.value)
            allReceipts.forEach { receipt ->
                try {
                    val file = java.io.File(receipt.imageUri)
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    Log.e("TripVM", "File deletion failed", e)
                }
            }
            repository.deleteTripById(_currentTripId.value)
        }
    }

    fun addReceipt(date: String, imagePath: String, amount: Double, notes: String) {
        viewModelScope.launch {
            val receipt = Receipt(
                tripId = _currentTripId.value,
                date = date,
                imageUri = imagePath,
                amount = amount,
                notes = notes
            )
            repository.insertReceipt(receipt)

            val currentTrip = uiState.value.trip
            val newTotal = currentTrip.totalAmount + amount
            repository.updateTrip(currentTrip.copy(totalAmount = newTotal))
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