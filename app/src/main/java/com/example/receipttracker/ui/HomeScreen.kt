package com.example.receipttracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.receipttracker.data.Trip
import com.example.receipttracker.ui.theme.ReceiptTrackerTheme

private val tripsForPreview = listOf(
    Trip(23, "SANSHOME", "01/02/23", endDate = "08/03/1993", 400.00),
    Trip(24, "Defcon33HOME", "05/08/25", endDate = "08/03/1993", 20.00),
    Trip(25, "Work TripHOIME", "01/02/25", endDate = "08/03/1993", 4888.86)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTripClick: () -> Unit,
    onViewTripClick: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.headlineMedium,
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddTripClick() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Trip")
            }
        }) { innerPadding ->
        TripList(
            items = tripsForPreview,
            onTripClick = onViewTripClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ReceiptTrackerTheme {
        HomeScreen(
            {},
            {}
        )
    }
}