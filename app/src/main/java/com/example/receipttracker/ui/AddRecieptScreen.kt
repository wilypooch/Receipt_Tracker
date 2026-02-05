package com.example.receipttracker.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.receipttracker.R
import com.example.receipttracker.data.AppCurrency.Companion.symbolFromCode
import com.example.receipttracker.data.ReceiptType
import com.example.receipttracker.ui.utils.ReceiptDatePicker
import com.example.receipttracker.ui.utils.ReceiptTypeDropdown
import com.example.receipttracker.ui.utils.convertMillisToDate
import com.example.receipttracker.ui.utils.copyUriToFile
import com.example.receipttracker.ui.utils.createImageFile
import java.io.File
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReceiptScreen(
    viewModel: TripDetailsViewModel,
    currencyCode: String,
    onNavigateUp: (String?) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var receiptType by remember { mutableStateOf(ReceiptType.FoodAndDrink) }
    var amount by remember { mutableStateOf("") }
    val isAmountValid = amount.toDoubleOrNull() != null
    var notes by remember { mutableStateOf("") }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val imageFile = File(currentPhotoPath!!)
            capturedImageUri = Uri.fromFile(imageFile)
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val localFile = context.createImageFile()
            context.copyUriToFile(uri, localFile)

            currentPhotoPath = localFile.absolutePath
            capturedImageUri = Uri.fromFile(localFile)
        }
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val tripStartMillis = uiState.trip.startDate
    val tripEndMillis = uiState.trip.endDate

    var selectedDate by remember {
        val today = System.currentTimeMillis()
        val initial = if (today in tripStartMillis..tripEndMillis) today else tripStartMillis
        mutableLongStateOf(initial)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate,
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
    val handleBackNavigation = { onNavigateUp(null) }
    BackHandler(onBack = handleBackNavigation)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Receipt") },
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
                    IconButton(
                        onClick = {
                            viewModel.addReceipt(
                                date = selectedDate,
                                imagePath = currentPhotoPath!!,
                                receiptType = receiptType.toString(),
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                notes = notes
                            )
                            onNavigateUp("saved")
                        },
                        enabled = capturedImageUri != null && isAmountValid,
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_save),
                            contentDescription = "Save"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Show preview if photo taken
            if (capturedImageUri != null) {
                AsyncImage(
                    model = capturedImageUri,
                    contentDescription = "Receipt Preview",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
            } else {
                Button(
                    onClick = {
                        val photoFile = context.createImageFile()
                        photoFile.let { file ->
                            currentPhotoPath = file.absolutePath
                            val photoUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            cameraLauncher.launch(photoUri)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Photo of Receipt")
                }
                Button(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select from Gallery")
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = convertMillisToDate(selectedDate),
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
                modifier = Modifier.onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        showDatePicker = true
                        focusManager.clearFocus()
                    }
                }
            )
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        datePickerState.selectedDateMillis = selectedDate
                        showDatePicker = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    selectedDate = millis
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }, dismissButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis =
                                selectedDate
                            showDatePicker = false
                        }) {
                            Text("Cancel")
                        }
                    }
                ) { ReceiptDatePicker(state = datePickerState) }
            }

            ReceiptTypeDropdown(
                selectedReceiptType = receiptType,
                onReceiptTypeSelected = { newType -> receiptType = newType })


            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (${symbolFromCode(currencyCode)})") },
                isError = amount.isNotEmpty() && !isAmountValid,
                supportingText = {
                    if (amount.isNotEmpty() && !isAmountValid) {
                        Text("Please enter a valid number")
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("Optional") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}