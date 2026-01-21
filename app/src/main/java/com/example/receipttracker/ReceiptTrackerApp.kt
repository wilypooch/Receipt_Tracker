package com.example.receipttracker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.receipttracker.ui.AddReceiptScreen
import com.example.receipttracker.ui.EditTripScreen
import com.example.receipttracker.ui.HomeScreen
import com.example.receipttracker.ui.HomeViewModel
import com.example.receipttracker.ui.ReceiptDetailScreen
import com.example.receipttracker.ui.ReceiptViewModel
import com.example.receipttracker.ui.TripDetailsViewModel
import com.example.receipttracker.ui.TripOverviewScreen
import java.util.UUID

data object TripList
data class TripOverview(val id: Int)
data class EditTrip(val id: Int)
data class AddReceipt(val tripId: Int)
data class ReceiptDetail(val receiptId: Int, val tripStartDate: String, val tripEndDate: String)


@Suppress("UNCHECKED_CAST")
@Composable
fun ReceiptTrackerApp() {
    val backStack = remember { mutableStateListOf<Any>(TripList) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val application = context.applicationContext as ReceiptTrackerApplication
    val repository = application.container.trackerRepository
    NavDisplay(
        backStack = backStack,
        // TODO: fix this onBack implementation as it is not currently used within the entry provider and code is duplicated.
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is TripList -> NavEntry(key) {
                    // TODO: Abstract this ViewModel Factory implementation away from the NavDisplay
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return HomeViewModel(application.container.trackerRepository) as T
                            }
                        }
                    )
                    HomeScreen(
                        viewModel = homeViewModel,
                        onViewTripClick = { selectedTripId ->
                            backStack.add(
                                TripOverview(
                                    selectedTripId
                                )
                            )
                        },
                        onAddTripClick = { backStack.add(EditTrip(-1)) },
                        snackbarMessage = snackbarMessage,
                        onSnackbarShown = { snackbarMessage = null }
                    )
                }

                is TripOverview -> NavEntry(key) {
                    val tripId = key.id
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
                    val start = viewModel.uiState.value.trip.startDate
                    val endDate = viewModel.uiState.value.trip.endDate
                    TripOverviewScreen(
                        viewModel = viewModel,
                        onNavigateUp = { result ->
                            if (result == "deleted") {
                                snackbarMessage = "Trip Deleted"
                            }
                            backStack.removeLastOrNull()
                        },
                        onAddReceiptClick = { backStack.add(AddReceipt(tripId)) },
                        onEditTripClick = { backStack.add(EditTrip(tripId)) },
                        onNavigateToReceipt = { receiptId ->
                            backStack.add(
                                ReceiptDetail(
                                    receiptId,
                                    start,
                                    endDate
                                )
                            )
                        },
                        snackbarMessage = snackbarMessage,
                        onSnackbarShown = { snackbarMessage = null }
                    )
                }

                is EditTrip -> NavEntry(key) {
                    val tripId = key.id
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
                    EditTripScreen(
                        viewModel = viewModel,
                        onNavigateUp = { result ->
                            when (result) {
                                "saved" -> {
                                    snackbarMessage = "Trip Saved"
                                    backStack.removeLastOrNull()
                                }

                                "deleted" -> {
                                    snackbarMessage = "Trip Deleted"
                                    backStack.retainAll { it is TripList }
                                }

                                else -> backStack.removeLastOrNull()
                            }
                        },
                    )
                }

                is AddReceipt -> NavEntry(key) {
                    val tripId = key.tripId
                    val viewModel: TripDetailsViewModel = viewModel(
                        // Reusing Trip ViewModel
                        key = "TripDetailVM_$tripId",
                        factory = TripDetailsViewModel.provideFactory(tripId, repository)
                    )
                    AddReceiptScreen(
                        viewModel = viewModel,
                        onReceiptSaved = { backStack.removeLastOrNull() }
                    )
                }

                is ReceiptDetail -> NavEntry(key) {
                    val receiptId = key.receiptId
                    val tripStartDate = key.tripStartDate
                    val tripEndDate = key.tripEndDate
                    val viewModel: ReceiptViewModel = viewModel(
                        key = "ReceiptDetailVM_$receiptId",
                        factory = ReceiptViewModel.provideFactory(receiptId, repository)
                    )
                    ReceiptDetailScreen(
                        viewModel = viewModel,
                        tripStartDate = tripStartDate,
                        tripEndDate = tripEndDate,
                        onNavigateUp = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Unit) { Text("Unknown Route") }
            }
        }
    )
}
