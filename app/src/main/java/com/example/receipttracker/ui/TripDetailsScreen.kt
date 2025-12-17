package com.example.receipttracker.ui

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.receipttracker.ui.theme.ReceiptTrackerTheme
import com.example.receipttracker.ui.utils.DeleteAlertDialog
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

fun convertDateStringToMillis(dateString: String): Long? {
    if (dateString.isBlank()) return null
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return try {
        formatter.parse(dateString)?.time
    } catch (_: Exception) {
        null
    }
}

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
    val datePickerState = rememberDateRangePickerState()
    val startMillis = remember(uiState.startDate) {
        convertDateStringToMillis(uiState.startDate)
    }
    val endMillis = remember(uiState.endDate) {
        convertDateStringToMillis(uiState.endDate)
    }
    LaunchedEffect(startMillis, endMillis) {
        if (startMillis != null && endMillis != null) {
            datePickerState.setSelection(startMillis, endMillis)
        }
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Trip Details")
        OutlinedTextField(
            value = uiState.name,
            label = { Text("Trip Name") },
            onValueChange = onNameChange,
        )
        OutlinedTextField(
            value = uiState.startDate,
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
            value = uiState.endDate,
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
            onClick = { showDeleteDialog = true }
        ) {
            Text("Delete")
        }
        if (showDeleteDialog) {
            DeleteAlertDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirmDelete = onDeleteClick
            )
        }
    }
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

@Composable
fun TripDateRangePicker(state: DateRangePickerState) {
    DateRangePicker(
        state = state,
        title = {
            Text(
                text = "Select trip dates", modifier = Modifier.padding(16.dp)
            )
        }, headline = {
            Row(modifier = Modifier.padding(16.dp)) {
                Box(Modifier.weight(1f)) {
                    (state.selectedStartDateMillis?.let { convertMillisToDate(it) }
                        ?: "Start Date").let { Text(it) }
                }
                Box(Modifier.weight(1f)) {
                    (state.selectedEndDateMillis?.let { convertMillisToDate(it) }
                        ?: "End Date").let { Text(it) }
                }
            }
        },
        showModeToggle = true,
        modifier = Modifier.height(height = 500.dp)
    )
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