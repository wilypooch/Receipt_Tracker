package com.example.receipttracker.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TripDetailScreen(tripId: Int, onNavigateUp: () -> Any?) {
    Text(text = "Trip Details for $tripId")
}