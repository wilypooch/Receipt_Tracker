package com.example.receipttracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receipttracker.R
import com.example.receipttracker.data.Trip
import com.example.receipttracker.ui.theme.ReceiptTrackerTheme
import com.example.receipttracker.ui.utils.DeleteAlertDialog
import com.example.receipttracker.ui.utils.ItemToBeDeleted
import com.example.receipttracker.ui.utils.TripDateRangePicker
import com.example.receipttracker.ui.utils.UnsavedChangesDialog
import com.example.receipttracker.ui.utils.convertDateStringToMillis
import com.example.receipttracker.ui.utils.convertMillisToDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(
    viewModel: TripDetailsViewModel,
    onNavigateUp: (String?) -> Unit,
) {
    val draft by viewModel.draftTrip.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startEditing()
    }
    val tripToDisplay = draft ?: uiState.trip
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    val hasUnsavedChanges = remember(draft, uiState.trip) {
        draft != null && draft != uiState.trip
    }

    val handleBackNavigation = {
        if (hasUnsavedChanges) {
            showUnsavedDialog = true
        } else {
            if (tripToDisplay.name.isBlank() && uiState.receipts.isEmpty()) {
                viewModel.deleteTrip()
            }
            viewModel.cancelEditing()
            onNavigateUp(null)
        }
    }

    BackHandler(onBack = handleBackNavigation)

    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            onConfirmDiscard = {
                showUnsavedDialog = false
                if (tripToDisplay.name.isBlank() && uiState.receipts.isEmpty()) {
                    viewModel.deleteTrip()
                }
                viewModel.cancelEditing()
                onNavigateUp(null)
            },
            onDismiss = { showUnsavedDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        handleBackNavigation()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    val isTripValid = tripToDisplay.name.isNotBlank() &&
                            tripToDisplay.startDate.isNotBlank() &&
                            tripToDisplay.endDate.isNotBlank()
                    IconButton(
                        onClick = {
                            viewModel.saveTrip()
                            onNavigateUp("saved")
                        },
                        // TODO: Also disable unless changes have been made
                        enabled = isTripValid
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_save),
                            contentDescription = "Save"
                        )
                    }
                    if (tripToDisplay.tripId > 0) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                    if (showDeleteDialog) {
                        DeleteAlertDialog(
                            item = ItemToBeDeleted.Trip,
                            onDismiss = { showDeleteDialog = false },
                            onConfirmDelete = {
                                viewModel.deleteTrip()
                                onNavigateUp("deleted")
                            }
                        )
                    }
                }
            )
        },

        ) { innerPadding ->
        // TODO: Add functionality to deal with screen rotation / different screen sizes
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TripDetailContent(
                trip = tripToDisplay,
                onNameChange = viewModel::onNameChange,
                onStartDateChange = viewModel::onStartDateChange,
                onEndDateChange = viewModel::onEndDateChange,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun TripDetailContent(
    trip: Trip,
    onNameChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    modifier: Modifier,
) {
    val datePickerState = rememberDateRangePickerState()
    val startMillis = remember(trip.startDate) {
        convertDateStringToMillis(trip.startDate)
    }
    val endMillis = remember(trip.endDate) {
        convertDateStringToMillis(trip.endDate)
    }
    LaunchedEffect(startMillis, endMillis) {
        if (startMillis != null && endMillis != null) {
            datePickerState.setSelection(startMillis, endMillis)
        }
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = trip.name,
            label = { Text("Trip Name") },
            onValueChange = onNameChange,
        )
        OutlinedTextField(
            value = trip.startDate,
            label = { Text("Start Date") },
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painterResource(R.drawable.ic_calendar_today),
                        contentDescription = "Select Start Date"
                    )
                }
            },
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    showDatePicker = true
                    focusManager.clearFocus()
                }
            }
        )
        OutlinedTextField(
            value = trip.endDate,
            label = { Text("End Date") },
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painterResource(R.drawable.ic_calendar_today),
                        contentDescription = "Select End Date"
                    )
                }
            },
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    showDatePicker = true
                    focusManager.clearFocus()
                }
            }
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val startMillis = datePickerState.selectedStartDateMillis
                            val endMillis = datePickerState.selectedEndDateMillis

                            if (startMillis != null) {
                                onStartDateChange(convertMillisToDate(startMillis))
                            }
                            if (endMillis != null) {
                                onEndDateChange(convertMillisToDate(endMillis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) { TripDateRangePicker(state = datePickerState) }
        }
    }
}

@Preview(showBackground = true, name = "Existing Trip")
@Composable
fun TripDetailsContentPreview() {
    val sampleTrip =
        Trip(
            name = "Rust Training In Manchester",
            startDate = "01/10/2025",
            endDate = "07/10/2025",
            totalAmount = 2500.00
        )

    ReceiptTrackerTheme {
        TripDetailContent(
            trip = sampleTrip,
            onNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            modifier = Modifier,
        )
    }
}

@Preview(showBackground = true, name = "New Trip (Empty)")
@Composable
fun TripDetailNewPreview() {
    ReceiptTrackerTheme {
        TripDetailContent(
            trip = Trip(),
            onNameChange = {},
            onStartDateChange = {},
            onEndDateChange = {},
            modifier = Modifier,
        )
    }
}