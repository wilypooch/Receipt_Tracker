package com.example.receipttracker.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.receipttracker.ui.utils.ReceiptDatePicker
import com.example.receipttracker.ui.utils.convertDateStringToMillis
import com.example.receipttracker.ui.utils.convertMillisToDate
import com.example.receipttracker.ui.utils.copyUriToFile
import com.example.receipttracker.ui.utils.createImageFile
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReceiptScreen(
    tripId: Int,
    viewModel: TripDetailsViewModel,
    onReceiptSaved: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
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
    val tripStartMillis =
        convertDateStringToMillis(viewModel.uiState.value.trip.startDate) ?: Long.MIN_VALUE
    val tripEndMillis =
        convertDateStringToMillis(viewModel.uiState.value.trip.endDate) ?: Long.MAX_VALUE
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    val dateToday = LocalDate.now().format(formatter)

    val initialDate = remember {
        val todayMillis = convertDateStringToMillis(dateToday) ?: 0L
        if (todayMillis in tripStartMillis..tripEndMillis) {
            dateToday
        } else {
            ""
        }
    }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertDateStringToMillis(selectedDate),
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


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Add Receipt") })
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
                value = selectedDate,
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
                        datePickerState.selectedDateMillis =
                            convertDateStringToMillis(selectedDate)
                        showDatePicker = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    selectedDate = convertMillisToDate(millis)
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }, dismissButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis =
                                convertDateStringToMillis(selectedDate)
                            showDatePicker = false
                        }) {
                            Text("Cancel")
                        }
                    }
                ) { ReceiptDatePicker(state = datePickerState) }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                isError = amount.isNotEmpty() && !isAmountValid,
                supportingText = {
                    if (amount.isNotEmpty() && !isAmountValid) {
                        Text("Please enter a valid number")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = capturedImageUri != null && selectedDate != "" && isAmountValid,
                onClick = {
                    viewModel.addReceipt(
                        tripId = tripId,
                        date = selectedDate,
                        imagePath = currentPhotoPath!!,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        notes = notes
                    )
                    onReceiptSaved()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Receipt")
            }
        }
    }
}