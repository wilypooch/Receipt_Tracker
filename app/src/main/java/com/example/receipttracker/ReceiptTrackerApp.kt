package com.example.receipttracker

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
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
sealed interface Route : NavKey {

    @Serializable
    data object TripList : Route, NavKey

    @Serializable
    data class TripOverview(val id: Int) : Route, NavKey

    @Serializable
    data class EditTrip(val id: Int) : Route, NavKey

    @Serializable
    data class AddReceipt(val tripId: Int, val currencyCode: String) : Route, NavKey

    @Serializable
    data class EditReceipt(
        val receiptId: Int,
        val tripStartDate: String,
        val tripEndDate: String,
        val currencyCode: String,
    ) : Route, NavKey
}


@Suppress("UNCHECKED_CAST")
@Composable
fun ReceiptTrackerApp(windowSize: WindowWidthSizeClass) {
    val backStack = rememberNavBackStack(Route.TripList)
    var snackbarMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val application = context.applicationContext as ReceiptTrackerApplication
    val repository = application.container.trackerRepository
    NavDisplay(
        backStack = backStack,
        // TODO: fix this onBack implementation as it is not currently used within the entry provider and code is duplicated.
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.TripList> { key ->
                NavEntry(key) {
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
                                Route.TripOverview(
                                    selectedTripId
                                )
                            )
                        },
                        onAddTripClick = { backStack.add(Route.EditTrip(-1)) },
                        snackbarMessage = snackbarMessage,
                        onSnackbarShown = { snackbarMessage = null }
                    )
                }
            }
            entry<Route.TripOverview> { key ->
                NavEntry(key) {
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
                        onAddReceiptClick = { currencyCode ->
                            backStack.add(
                                Route.AddReceipt(
                                    tripId,
                                    currencyCode
                                )
                            )
                        },
                        onEditTripClick = { backStack.add(Route.EditTrip(tripId)) },
                        onNavigateToReceipt = { receiptId, currencyCode ->
                            backStack.add(
                                Route.EditReceipt(
                                    receiptId,
                                    start,
                                    endDate,
                                    currencyCode
                                )
                            )
                        },
                        snackbarMessage = snackbarMessage,
                        onSnackbarShown = { snackbarMessage = null }
                    )
                }
            }
            entry<Route.EditTrip> { key ->
                NavEntry(key) {
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
                                    backStack.retainAll { it is Route.TripList }
                                }

                                else -> backStack.removeLastOrNull()
                            }
                        },
                    )
                }
            }
            entry<Route.AddReceipt> { key ->
                NavEntry(key) {
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
            }
            entry<Route.EditReceipt> { key ->
                NavEntry(key) {
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
        }
    )
}
