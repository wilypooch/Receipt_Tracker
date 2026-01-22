package com.example.receipttracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.receipttracker.R
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.ui.utils.DeleteAlertDialog
import com.example.receipttracker.ui.utils.ItemToBeDeleted
import com.example.receipttracker.ui.utils.ReceiptDatePicker
import com.example.receipttracker.ui.utils.UnsavedChangesDialog
import com.example.receipttracker.ui.utils.convertDateStringToMillis
import com.example.receipttracker.ui.utils.convertMillisToDate
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptDataScreen(
    viewModel: ReceiptViewModel,
    tripStartDate: String,
    tripEndDate: String,
    onNavigateUp: (String?) -> Unit,
) {
    val draft by viewModel.draftReceipt.collectAsState()
    val uiState by viewModel.receiptState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.startEditing()
    }
    val receiptToDisplay = draft ?: uiState
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    val hasUnsavedChanges = remember(draft, uiState) { draft != null && draft != uiState }

    val handleBackNavigation = {
        if (hasUnsavedChanges) {
            showUnsavedDialog = true
        } else {
            viewModel.cancelEditing()
            onNavigateUp(null)
        }
    }

    BackHandler(onBack = handleBackNavigation)

    if (showUnsavedDialog) {
        UnsavedChangesDialog(
            onConfirmDiscard = {
                showUnsavedDialog = false
                viewModel.cancelEditing()
                onNavigateUp(null)
            },
            onDismiss = { showUnsavedDialog = false })
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Receipt Details") },
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
                if (receiptToDisplay != null) {
                    val isReceiptValid = receiptToDisplay.date.isNotBlank() &&
                            receiptToDisplay.amount > 0.0 &&
                            receiptToDisplay.imageUri.isNotBlank()

                    IconButton(
                        onClick = {
                            viewModel.updateReceipt()
                            onNavigateUp("saved")
                        },
                        enabled = isReceiptValid && hasUnsavedChanges
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_save),
                            contentDescription = "Save"
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete"
                        )
                    }
                    if (showDeleteDialog) {
                        DeleteAlertDialog(
                            item = ItemToBeDeleted.Receipt,
                            onDismiss = { showDeleteDialog = false },
                            onConfirmDelete = {
                                viewModel.deleteReceipt(receiptToDisplay.tripId)
                                onNavigateUp("deleted")
                            }
                        )
                    }
                }
            }
        )
    }
    ) { innerPadding ->
        if (uiState == null) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // TODO: Add functionality to deal with screen rotation / different screen sizes
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                EditReceiptDataContent(
                    receipt = uiState!!,
                    tripStartDate = tripStartDate,
                    tripEndDate = tripEndDate,
                    onDateChange = viewModel::onDateChange,
                    onAmountChange = viewModel::onAmountChange,
                    onNotesChange = viewModel::onNotesChange,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
fun EditReceiptDataContent(
    receipt: Receipt,
    tripStartDate: String,
    tripEndDate: String,
    onDateChange: (String) -> Unit,
    onAmountChange: (Double) -> Unit,
    onNotesChange: (String) -> Unit,
    modifier: Modifier,
) {
    var amountText by remember { mutableStateOf(receipt.amount.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val tripStartMillis = convertDateStringToMillis(tripStartDate) ?: Long.MIN_VALUE
    val tripEndMillis = convertDateStringToMillis(tripEndDate) ?: Long.MAX_VALUE
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertDateStringToMillis(receipt.date),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in tripStartMillis..tripEndMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = tripStartMillis
                val startYear = calendar.get(Calendar.YEAR)
                calendar.timeInMillis = tripEndMillis
                val endYear = calendar.get(Calendar.YEAR)
                return year in startYear..endYear
            }
        }
    )
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = receipt.imageUri,
            contentDescription = "Receipt Image",
            modifier = Modifier
                .height(400.dp)
        )
        OutlinedTextField(
            value = receipt.date,
            label = { Text("Receipt Date") },
            readOnly = true,
            onValueChange = { },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painterResource(R.drawable.ic_calendar_today),
                        contentDescription = "Select Receipt Date"
                    )
                }
            },
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    datePickerState.selectedDateMillis = convertDateStringToMillis(receipt.date)
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDate = datePickerState.selectedDateMillis
                            if (selectedDate != null) {
                                onDateChange(convertMillisToDate(selectedDate))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis = convertDateStringToMillis(receipt.date)
                        showDatePicker = false
                    }) {
                        Text("Cancel")
                    }
                }
            ) { ReceiptDatePicker(state = datePickerState) }
        }
        OutlinedTextField(
            value = amountText,
            label = { Text("Receipt Amount") },
            onValueChange = { newText ->
                amountText = newText
                val parsed = newText.toDoubleOrNull()
                if (parsed != null) {
                    onAmountChange(parsed)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            value = receipt.notes,
            label = { Text("Notes") },
            onValueChange = onNotesChange
        )
    }
}