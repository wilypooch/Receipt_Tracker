package com.example.receipttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val trips: List<Trip> = emptyList(),
)

class HomeViewModel(
    repository: TrackerRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        repository.getAllTripsByDateDescStream()
            .map { trips -> HomeUiState(trips = trips) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                HomeUiState()
            )

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(repository: TrackerRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}

