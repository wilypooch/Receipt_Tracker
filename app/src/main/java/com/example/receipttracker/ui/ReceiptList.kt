package com.example.receipttracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.receipttracker.data.Receipt

@Composable
fun ReceiptList(
    items: List<Receipt>,
    // TODO: Manage onClick and onDelete
    onReceiptClick: (Int) -> Unit,
    // TODO: Need to delete receipt photo from device as well as from database
    onDeleteReceipt: (Receipt) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            // TODO: Add swipe to delete functionality
            ReceiptListCard(
                receiptUri = item.imageUri,
                receiptDate = item.date,
                receiptAmount = item.amount,
                receiptNotes = item.notes,
                onClick = { TODO() }
            )
        }
    }
}


@Composable
fun ReceiptListCard(
    receiptUri: String,
    receiptDate: String,
    receiptAmount: Double,
    receiptNotes: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = onClick
    ) {
        Row(modifier = modifier.padding(16.dp)) {
            Column {
                AsyncImage(
                    model = receiptUri,
                    contentDescription = "Receipt Preview",
                    modifier = Modifier
                        .height(200.dp)
                )
            }
            Column {
                Row {
                    Column(modifier.weight(2f)) {
                        Text(text = "Receipt Date:", style = MaterialTheme.typography.labelMedium)
                    }
                    Column {
                        Text(text = receiptDate, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Row {
                    Column(modifier.weight(2f)) {
                        Text(text = "Total:", style = MaterialTheme.typography.labelMedium)
                    }
                    Column {
                        // TODO: Format currency
                        Text(
                            text = receiptAmount.toString(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Row {
                    Column(modifier.weight(2f)) {
                        Text(text = "Notes:", style = MaterialTheme.typography.labelMedium)
                    }
                    Column {
                        Text(text = receiptNotes, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}