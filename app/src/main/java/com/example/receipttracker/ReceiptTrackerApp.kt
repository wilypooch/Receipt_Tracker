package com.example.receipttracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

data object TripList
data class TripDetail(val id: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNCHECKED_CAST")
@Composable
fun ReceiptTrackerApp() {
    val backStack = remember { mutableStateListOf<Any>(TripList) }
    val currentScreen = backStack.lastOrNull() ?: TripList
    Scaffold(
        //TODO: Add adaptive top bar title functionality
        topBar = { CenterAlignedTopAppBar(title = { Text("PLACEHOLDER TITLE, CHANGE ME") }) },
        floatingActionButton = {
            if (currentScreen is TripList) {
                // TODO: perhaps change this ID, it doesn't feel right
                FloatingActionButton(onClick = { backStack.add(TripDetail(-1)) }) {
                    Icon(Icons.Filled.Add, "Add")
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
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
                            }
                        )
                    }

                    is TripDetail -> NavEntry(key) {
                        val tripId = key.id
                        val context = LocalContext.current
                        val application = context.applicationContext as ReceiptTrackerApplication
                        val repository = application.container.trackerRepository

                        val viewModel: TripDetailsViewModel =
                            viewModel(
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
}