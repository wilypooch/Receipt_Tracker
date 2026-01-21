package com.example.receipttracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.receipttracker.ui.utils.DeleteAlertDialog
import com.example.receipttracker.ui.utils.ItemToBeDeleted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripOverviewScreen(
    viewModel: TripDetailsViewModel,
    onNavigateUp: (String?) -> Unit,
    onEditTripClick: () -> Unit,
    onAddReceiptClick: () -> Unit,
    onNavigateToReceipt: (Int) -> Unit,
    snackbarMessage: String? = null,
    onSnackbarShown: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSnackbarShown()
        }
    }
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val handleBackNavigation = {
        if (uiState.trip.name.isBlank() && uiState.receipts.isEmpty()) {
            viewModel.deleteTrip()
        }
        onNavigateUp(null)
    }
    BackHandler(onBack = handleBackNavigation)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.trip.name) },
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
                            onEditTripClick()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    if (uiState.trip.tripId > 0) {
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
        floatingActionButton = {
            FloatingActionButton(onClick = onAddReceiptClick) {
                Icon(Icons.Filled.Add, "Add Receipt")
            }
        }
    ) { innerPadding ->
        val numReceipts = uiState.receipts.size
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(text = "${uiState.trip.startDate} - ${uiState.trip.endDate}")
            }
            Row(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)) {
                if (numReceipts != 1) {
                    Text(
                        text = "$numReceipts receipts"
                    )
                } else {
                    Text(
                        text = "$numReceipts receipt"
                    )
                }
            }
            ReceiptList(
                items = uiState.receipts,
                onReceiptClick = onNavigateToReceipt,
                modifier = Modifier.weight(1f)
            )
        }
    }

}