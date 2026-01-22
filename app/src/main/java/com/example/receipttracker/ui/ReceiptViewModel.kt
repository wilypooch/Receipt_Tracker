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

    val receiptState: StateFlow<Receipt?> =
        repository.getReceiptStream(receiptId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    private val _draftReceipt = MutableStateFlow<Receipt?>(null)
    val draftReceipt: StateFlow<Receipt?> = _draftReceipt.asStateFlow()

    init {
        viewModelScope.launch {
            receiptState.collect { dbReceipt ->
                // Only initialize edits if user hasn't started editing yet
                if (dbReceipt != null && _draftReceipt.value == null) {
                    _draftReceipt.value = dbReceipt
                }
            }
        }
    }

    fun startEditing() {
        _draftReceipt.value = receiptState.value
    }

    fun cancelEditing() {
        _draftReceipt.value = null
    }

    fun onDateChange(newDate: String) {
        _draftReceipt.update { it?.copy(date = newDate) }
    }

    fun onAmountChange(newAmount: Double) {
        _draftReceipt.update { it?.copy(amount = newAmount) }
    }

    fun onNotesChange(newNotes: String) {
        _draftReceipt.update { it?.copy(notes = newNotes) }
    }

    fun updateReceipt() {
        viewModelScope.launch {
            _draftReceipt.value?.let { repository.updateReceipt(it) }
        }
    }

    fun deleteReceipt(tripId: Int) {
        viewModelScope.launch {
            val receiptToDelete = receiptState.value ?: return@launch
            try {
                val file = java.io.File(receiptToDelete.imageUri)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                Log.e("ReceiptVM", "Failed to delete image file", e)
            }
            repository.deleteReceiptAndUpdateTripTotal(receiptId, tripId)
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