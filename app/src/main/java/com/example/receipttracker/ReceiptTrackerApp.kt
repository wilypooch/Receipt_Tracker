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
import com.example.receipttracker.ui.AddReceiptScreen
import com.example.receipttracker.ui.HomeScreen
import com.example.receipttracker.ui.HomeViewModel
import com.example.receipttracker.ui.ReceiptDetailScreen
import com.example.receipttracker.ui.ReceiptViewModel
import com.example.receipttracker.ui.TripDetailScreen
import com.example.receipttracker.ui.TripDetailsViewModel
import java.util.UUID

data object TripList
data class TripDetail(val id: Int)
data class AddReceipt(val tripId: Int)
data class ReceiptDetail(val receiptId: Int)


@Suppress("UNCHECKED_CAST")
@Composable
fun ReceiptTrackerApp() {
    val backStack = remember { mutableStateListOf<Any>(TripList) }
    NavDisplay(
        backStack = backStack,
        // TODO: fix this onBack implementation as it is not currently used within the entry provider and code is duplicated.
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is TripList -> NavEntry(key) {
                    val context = LocalContext.current
                    val app = context.applicationContext as ReceiptTrackerApplication
                    // TODO: Abstract this ViewModel Factory implementation away from the NavDisplay
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
                    // TODO: fix the duplication of this code here and in the AddReceipt implementation
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
                        onNavigateUp = { backStack.removeLastOrNull() },
                        onAddReceiptClick = {
                            backStack.add(AddReceipt(tripId))
                        },
                        onNavigateToReceipt = { receiptId -> backStack.add(ReceiptDetail(receiptId)) }
                    )
                }

                is AddReceipt -> NavEntry(key) {
                    val tripId = key.tripId
                    val context = LocalContext.current
                    val application = context.applicationContext as ReceiptTrackerApplication
                    val repository = application.container.trackerRepository

                    // Reusing Trip ViewModel
                    val viewModelKey = "TripDetailVM_$tripId"

                    val viewModel: TripDetailsViewModel = viewModel(
                        key = viewModelKey,
                        factory = TripDetailsViewModel.provideFactory(tripId, repository)
                    )
                    AddReceiptScreen(
                        tripId = tripId,
                        viewModel = viewModel,
                        onReceiptSaved = { backStack.removeLastOrNull() }
                    )
                }

                is ReceiptDetail -> NavEntry(key) {
                    val receiptId = key.receiptId
                    val context = LocalContext.current
                    val application = context.applicationContext as ReceiptTrackerApplication
                    val repository = application.container.trackerRepository

                    val viewModel: ReceiptViewModel = viewModel(
                        key = "ReceiptDetailVM_$receiptId",
                        factory = ReceiptViewModel.provideFactory(receiptId, repository)
                    )

                    ReceiptDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Unit) { Text("Unknown Route") }
            }
        }
    )
}
