package com.example.receipttracker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.receipttracker.ui.HomeScreen
import com.example.receipttracker.ui.HomeViewModel
import com.example.receipttracker.ui.TripDetailScreen
import com.example.receipttracker.ui.TripDetailsViewModel
import java.util.UUID

data object TripList
data class TripDetail(val id: Int)

@Suppress("UNCHECKED_CAST")
@Composable
fun ReceiptTrackerApp() {
    val backStack = remember { mutableStateListOf<Any>(TripList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is TripList -> NavEntry(key) {
                    val context = LocalContext.current
                    val app = context.applicationContext as ReceiptTrackerApplication
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return HomeViewModel(app.container.trackerRepository) as T
                            }
                        }
                    )
                    HomeScreen(
                        viewModel = homeViewModel,
                        onViewTripClick = { selectedTripId ->
                            backStack.add(
                                TripDetail(
                                    selectedTripId
                                )
                            )
                        },
                        onAddTripClick = { backStack.add(TripDetail(-1)) }
                    )
                }

                is TripDetail -> NavEntry(key) {
                    val tripId = key.id
                    val context = LocalContext.current
                    val application = context.applicationContext as ReceiptTrackerApplication
                    val repository = application.container.trackerRepository

                    val viewModelKey = if (tripId == -1) {
                        "TripDetailVM_New_${UUID.randomUUID()}"
                    } else {
                        "TripDetailVM_$tripId"
                    }
                    val viewModel: TripDetailsViewModel =
                        viewModel(
                            key = viewModelKey,
                            factory = TripDetailsViewModel.provideFactory(
                                tripId,
                                repository
                            )
                        )
                    TripDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Unit) { Text("Unknown Route") }
            }
        }
    )
}
