package com.example.receipttracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receipttracker.data.Trip
import com.example.receipttracker.ui.theme.ReceiptTrackerTheme

@Composable
fun TripList(
    items: List<Trip>,
    onTripClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            // TODO: Add swipe to delete functionality ?
            TripListCard(
                tripName = item.name,
                tripStartDate = item.startDate,
                tripTotal = item.totalAmount,
                onClick = { onTripClick(item.tripId) }
            )
        }
    }
}

@Composable
fun TripListCard(
    tripName: String,
    tripStartDate: String,
    tripTotal: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = onClick
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Row {
                Text(tripName, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier.height(8.dp))
            Row {
                Column(modifier.weight(2f)) {
                    Text(text = "Start Date:", style = MaterialTheme.typography.labelMedium)
                }
                Column {
                    Text(text = tripStartDate, style = MaterialTheme.typography.labelMedium)
                }
            }
            Row {
                Column(modifier.weight(2f)) {
                    Text(text = "Total:", style = MaterialTheme.typography.labelMedium)
                }
                Column {
                    // TODO: Format currency
                    Text(text = tripTotal.toString(), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripListPreview() {
    ReceiptTrackerTheme {
        TripList(
            items = listOf(
                Trip(23, "SANS", "01/02/23", endDate = "08/03/1993", "GBP", 400.00),
                Trip(24, "Defcon33", "05/08/25", endDate = "08/03/1993","GBP", 20.00),
                Trip(25, "Work Trip", "01/02/25", endDate = "08/03/1993", "GBP", 4888.86)
            ), onTripClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripListCardPreview() {
    ReceiptTrackerTheme {
        TripListCard("Sample Name", tripStartDate = "01/01/1970", tripTotal = 500.00, onClick = {})
    }
}