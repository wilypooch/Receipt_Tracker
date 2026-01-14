package com.example.receipttracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.ui.utils.DeleteAlertDialog
import com.example.receipttracker.ui.utils.ItemToBeDeleted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptDetailScreen(
    viewModel: ReceiptViewModel,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Receipt Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        val edits by viewModel.userEdits.collectAsState()

        if (edits == null) {
            Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
        } else {
            // TODO: Add functionality to deal with screen rotation / different screen sizes
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                ReceiptDetailContent(
                    receipt = edits!!,
                    onDateChange = viewModel::onDateChange,
                    onAmountChange = viewModel::onAmountChange,
                    onNotesChange = viewModel::onNotesChange,
                    onUpdateClick = {
                        viewModel.updateReceipt()
                        onNavigateUp()
                    },
                    onDeleteClick = {
                        viewModel.deleteReceipt()
                        onNavigateUp()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
fun ReceiptDetailContent(
    receipt: Receipt,
    onDateChange: (String) -> Unit,
    onAmountChange: (Double) -> Unit,
    onNotesChange: (String) -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier,
) {
    var amountText by remember { mutableStateOf(receipt.amount.toString()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: Add Async Image of Receipt Here

        OutlinedTextField(
            // TODO: Add Date Picker Functionality
            value = receipt.date,
            label = { Text("Receipt Date") },
            onValueChange = onDateChange,
        )

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

        Button(
            // TODO: Disable until fields are full
            onClick = onUpdateClick,
        ) {
            Text("Save")
        }

        Button(
            onClick = { showDeleteDialog = true }
            // TODO: Disable unless trip already in database
        ) {
            Text("Delete")
        }

        if (showDeleteDialog) {
            DeleteAlertDialog(
                item = ItemToBeDeleted.Receipt,
                onDismiss = { showDeleteDialog = false },
                onConfirmDelete = onDeleteClick
            )
        }
    }
}