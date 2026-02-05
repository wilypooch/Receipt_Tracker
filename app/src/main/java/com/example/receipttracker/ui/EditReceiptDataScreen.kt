package com.example.receipttracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.receipttracker.R
import com.example.receipttracker.data.AppCurrency.Companion.symbolFromCode
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.data.ReceiptType.Companion.receiptTypeFromString
import com.example.receipttracker.ui.utils.DeleteAlertDialog
import com.example.receipttracker.ui.utils.ItemToBeDeleted
import com.example.receipttracker.ui.utils.ReceiptDatePicker
import com.example.receipttracker.ui.utils.ReceiptTypeDropdown
import com.example.receipttracker.ui.utils.UnsavedChangesDialog
import com.example.receipttracker.ui.utils.convertMillisToDate
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptDataScreen(
    viewModel: ReceiptViewModel,
    windowSize: WindowWidthSizeClass,
    tripStartDate: Long,
    tripEndDate: Long,
    currencyCode: String,
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
                    val isReceiptValid = receiptToDisplay.date > 0 &&
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
        val scrollState = rememberScrollState()
        if (uiState == null) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                EditReceiptDataContent(
                    windowSize = windowSize,
                    receipt = receiptToDisplay!!,
                    tripStartDate = tripStartDate,
                    tripEndDate = tripEndDate,
                    currencyCode = currencyCode,
                    onDateChange = viewModel::onDateChange,
                    onReceiptTypeChange = viewModel::onReceiptTypeChange,
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
    windowSize: WindowWidthSizeClass,
    receipt: Receipt,
    tripStartDate: Long,
    tripEndDate: Long,
    currencyCode: String,
    onDateChange: (Long) -> Unit,
    onReceiptTypeChange: (String) -> Unit,
    onAmountChange: (Double) -> Unit,
    onNotesChange: (String) -> Unit,
    modifier: Modifier,
) {
    val isExpanded =
        windowSize == WindowWidthSizeClass.Expanded || windowSize == WindowWidthSizeClass.Medium

    val image = @Composable {
        AsyncImage(
            model = receipt.imageUri,
            contentDescription = "Receipt Image",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = if (isExpanded) 600.dp else 400.dp)
        )
    }

    val form = @Composable {
        ReceiptFormFields(
            receipt = receipt,
            tripStartDate = tripStartDate,
            tripEndDate = tripEndDate,
            currencyCode = currencyCode,
            onDateChange = onDateChange,
            onReceiptTypeChange = onReceiptTypeChange,
            onAmountChange = onAmountChange,
            onNotesChange = onNotesChange
        )
    }

    if (isExpanded) {
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            Box(modifier = Modifier.weight(1.2f)) { image() }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) { form() }
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            image()
            form()
        }
    }
}

@Composable
private fun ReceiptFormFields(
    receipt: Receipt,
    tripStartDate: Long,
    tripEndDate: Long,
    currencyCode: String,
    onDateChange: (Long) -> Unit,
    onReceiptTypeChange: (String) -> Unit,
    onAmountChange: (Double) -> Unit,
    onNotesChange: (String) -> Unit,
) {
    var amountText by remember { mutableStateOf(receipt.amount.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = receipt.date,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in tripStartDate..tripEndDate
            }

            override fun isSelectableYear(year: Int): Boolean {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = tripStartDate
                val startYear = calendar.get(Calendar.YEAR)
                calendar.timeInMillis = tripEndDate
                val endYear = calendar.get(Calendar.YEAR)
                return year in startYear..endYear
            }
        }
    )

    OutlinedTextField(
        value = convertMillisToDate(receipt.date),
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
                datePickerState.selectedDateMillis = receipt.date
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            onDateChange(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis = receipt.date
                    showDatePicker = false
                }) {
                    Text("Cancel")
                }
            }
        ) { ReceiptDatePicker(state = datePickerState) }
    }

    ReceiptTypeDropdown(
        selectedReceiptType = receiptTypeFromString(receipt.receiptType),
        onReceiptTypeSelected = { newType ->
            onReceiptTypeChange(newType.name)
        }
    )

    OutlinedTextField(
        value = amountText,
        label = { Text("Receipt Amount (${symbolFromCode(currencyCode)})") },
        onValueChange = { newText ->
            amountText = newText
            val parsed = newText.toDoubleOrNull()
            if (parsed != null) {
                onAmountChange(parsed)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
    )

    OutlinedTextField(
        value = receipt.notes,
        label = { Text("Notes") },
        placeholder = { Text("Optional") },
        onValueChange = onNotesChange
    )
}