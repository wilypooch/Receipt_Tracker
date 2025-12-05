package com.example.receipttracker.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.receipttracker.ui.theme.ReceiptTrackerTheme

@Composable
fun TripDetailScreen(tripId: Int, onNavigateUp: () -> Any?) {
    Text(text = "Trip Details for $tripId")
}

@Preview(showBackground = true)
@Composable
fun TripDetailsScreenPreview() {
    ReceiptTrackerTheme {
        TripDetailScreen(tripId = 67, onNavigateUp = {})
    }
}