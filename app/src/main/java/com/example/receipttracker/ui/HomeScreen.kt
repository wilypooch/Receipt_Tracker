package com.example.receipttracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onViewTripClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    TripList(
        items = uiState.trips,
        onTripClick = onViewTripClick,
        onDeleteTrip = viewModel::deleteTrip,
        modifier = modifier
    )
}