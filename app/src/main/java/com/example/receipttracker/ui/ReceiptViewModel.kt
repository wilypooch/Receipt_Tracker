package com.example.receipttracker.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.data.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReceiptViewModel(
    private val receiptId: Int,
    private val repository: TrackerRepository,
) : ViewModel() {

    val receiptState: StateFlow<Receipt?> = repository.getReceiptStream(receiptId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    private val _userEdits = MutableStateFlow<Receipt?>(null)

    val userEdits: StateFlow<Receipt?> = _userEdits.asStateFlow()

    init {
        viewModelScope.launch {
            receiptState.collect { dbReceipt ->
                // Only initialize edits if user hasn't started editing yet
                if (dbReceipt != null && _userEdits.value == null) {
                    _userEdits.value = dbReceipt
                }
            }
        }
    }

    fun onDateChange(newDate: String) {
        _userEdits.update { it?.copy(date = newDate) }
    }

    fun onAmountChange(newAmount: Double) {
        _userEdits.update { it?.copy(amount = newAmount) }
    }

    fun onNotesChange(newNotes: String) {
        _userEdits.update { it?.copy(notes = newNotes) }
    }

    fun updateReceipt() {
        viewModelScope.launch {
            _userEdits.value?.let { repository.updateReceipt(it) }
        }
    }

    fun deleteReceipt(tripId: Int) {
        viewModelScope.launch {
            val receiptToDelete = receiptState.value
            val amountToRemove = receiptToDelete?.amount ?: 0.0
            if (receiptToDelete != null) {
                try {
                    val file = java.io.File(receiptToDelete.imageUri)
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    Log.e("ReceiptVM", "Failed to delete image file", e)
                }
            }
            val actualTrip = repository.getTripById(tripId)
            if (actualTrip != null) {
                val newTotal =
                    (actualTrip.totalAmount - amountToRemove).coerceAtLeast(0.0)
                repository.updateTrip(actualTrip.copy(totalAmount = newTotal))
            }
            repository.deleteReceiptById(receiptId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            receiptId: Int,
            repository: TrackerRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReceiptViewModel(receiptId, repository) as T
                }
            }
    }
}