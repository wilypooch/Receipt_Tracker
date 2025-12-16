package com.example.receipttracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onViewTripClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.trips.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "No trips available.\nAdd one using the button below.",
                textAlign = TextAlign.Center
            )
        }

    } else {
        TripList(
            items = uiState.trips,
            onTripClick = onViewTripClick,
            onDeleteTrip = viewModel::deleteTrip,
            modifier = modifier
        )
    }
}