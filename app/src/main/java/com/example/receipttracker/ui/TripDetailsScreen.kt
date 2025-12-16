package com.example.receipttracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.receipttracker.R
import com.example.receipttracker.ui.theme.ReceiptTrackerTheme

@Composable
fun TripDetailScreen(viewModel: TripDetailsViewModel, onNavigateUp: () -> Unit) {
    val uiState by viewModel.currentTripUiState.collectAsState()

    TripDetailContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onAmountChange = viewModel::onTotalAmountChange,
        onSaveClick = {
            viewModel.saveTrip()
            onNavigateUp()
        },
        onDeleteClick = {
            // TODO: Add warning toast asking for confirmation
            viewModel.deleteTrip()
            onNavigateUp()
        }
    )
}

@Composable
fun TripDetailContent(
    uiState: TripDetailsUiState,
    onNameChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Trip Details")

        OutlinedTextField(
            value = uiState.name,
            label = { Text("Trip Name") },
            onValueChange = onNameChange,
        )

        OutlinedTextField(
            value = uiState.startDate,
            label = { Text("Start Date") },
            onValueChange = onStartDateChange,
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_calendar_today),
                    contentDescription = "Select Start Date"
                )
            }

        )

        OutlinedTextField(
            value = uiState.endDate,
            label = { Text("End Date") },
            onValueChange = onEndDateChange,
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_calendar_today),
                    contentDescription = "Select End Date"
                )
            }
        )

        OutlinedTextField(
            value = uiState.totalAmount,
            label = { Text("Total Amount") },
            onValueChange = onAmountChange,
        )

        Button(
            onClick = onSaveClick,
        ) {
            Text("Save")
        }

        Button(
            onClick = onDeleteClick,
        ) {
            Text("Delete")
        }
    }
}

@Preview(showBackground = true, name = "Existing Trip")
@Composable
fun TripDetailsContentPreview() {
    val sampleState = TripDetailsUiState(
        name = "Rust Training In Manchester",
        startDate = "01/10/2025",
        endDate = "07/10/2025",
        totalAmount = "2500.00"
    )

    ReceiptTrackerTheme {
        TripDetailContent(
            uiState = sampleState,
            onNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onAmountChange = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}

@Preview(showBackground = true, name = "New Trip (Empty)")
@Composable
fun TripDetailNewPreview() {
    ReceiptTrackerTheme {
        TripDetailContent(
            uiState = TripDetailsUiState(), // Default empty state
            onNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onAmountChange = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}