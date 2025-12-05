package com.example.receipttracker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.receipttracker.ui.HomeScreen

data object TripList
data class TripDetail(val id: Int)

@Composable
fun ReceiptTrackerApp() {
    val backStack = remember { mutableStateListOf<Any>(TripList) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is TripList -> NavEntry(key) {
                    HomeScreen(
                        // TODO: perhaps change this ID, it doesn't feel right
                        onAddTripClick = { backStack.add(TripDetail(-1)) },
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
                    TripDetailScreen(
                        tripId = tripId,
                        onNavigateUp = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Unit) { Text("Unknown Route") }
            }
        }
    )
}
}