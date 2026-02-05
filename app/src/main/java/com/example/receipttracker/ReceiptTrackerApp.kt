package com.example.receipttracker

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.receipttracker.ui.AddReceiptScreen
import com.example.receipttracker.ui.EditReceiptDataScreen
import com.example.receipttracker.ui.EditTripScreen
import com.example.receipttracker.ui.HomeScreen
import com.example.receipttracker.ui.HomeViewModel
import com.example.receipttracker.ui.ReceiptViewModel
import com.example.receipttracker.ui.TripDetailsViewModel
import com.example.receipttracker.ui.TripOverviewScreen
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface Route : NavKey

@Serializable
data object TripList : Route

@Serializable
data class TripOverview(val id: Int) : Route

@Serializable
data class EditTrip(val id: Int) : Route

@Serializable
data class AddReceipt(val tripId: Int, val currencyCode: String) : Route

@Serializable
data class EditReceipt(
    val receiptId: Int,
    val tripStartDate: Long,
    val tripEndDate: Long,
    val currencyCode: String,
) : Route


@Suppress("UNCHECKED_CAST")
@Composable
fun ReceiptTrackerApp(windowSize: WindowWidthSizeClass) {
    val backStack = rememberNavBackStack(TripList)
    var snackbarMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val application = context.applicationContext as ReceiptTrackerApplication
    val repository = application.container.trackerRepository
    NavDisplay(
        backStack = backStack,
        // TODO: fix this onBack implementation as it is not currently used within the entry provider and code is duplicated.
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<TripList> { key ->
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(repository)
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
            entry<TripOverview> { key ->
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
                val uiState by viewModel.uiState.collectAsState()
                TripOverviewScreen(
                    viewModel = viewModel,
                    onNavigateUp = { result ->
                        if (result == "deleted") {
                            snackbarMessage = "Trip Deleted"
                        }
                        backStack.removeLastOrNull()
                    },
                    onAddReceiptClick = { currencyCode ->
                        backStack.add(
                            AddReceipt(
                                tripId,
                                currencyCode
                            )
                        )
                    },
                    onEditTripClick = { backStack.add(EditTrip(tripId)) },
                    onNavigateToReceipt = { receiptId, currencyCode ->
                        backStack.add(
                            EditReceipt(
                                receiptId,
                                uiState.trip.startDate,
                                uiState.trip.endDate,
                                currencyCode
                            )
                        )
                    },
                    snackbarMessage = snackbarMessage,
                    onSnackbarShown = { snackbarMessage = null }
                )

            }
            entry<EditTrip> { key ->
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
            entry<AddReceipt> { key ->
                val tripId = key.tripId
                val currencyCode = key.currencyCode
                val viewModel: TripDetailsViewModel = viewModel(
                    // Reusing Trip ViewModel
                    key = "TripDetailVM_$tripId",
                    factory = TripDetailsViewModel.provideFactory(tripId, repository)
                )
                AddReceiptScreen(
                    viewModel = viewModel,
                    currencyCode = currencyCode,
                    onNavigateUp = { result ->
                        if (result == "saved") {
                            snackbarMessage = "Receipt Saved"
                        }
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<EditReceipt> { key ->
                val receiptId = key.receiptId
                val tripStartDate = key.tripStartDate
                val tripEndDate = key.tripEndDate
                val currencyCode = key.currencyCode
                val viewModel: ReceiptViewModel = viewModel(
                    key = "ReceiptDetailVM_$receiptId",
                    factory = ReceiptViewModel.provideFactory(receiptId, repository)
                )
                EditReceiptDataScreen(
                    viewModel = viewModel,
                    windowSize = windowSize,
                    tripStartDate = tripStartDate,
                    tripEndDate = tripEndDate,
                    currencyCode = currencyCode,
                    onNavigateUp = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
