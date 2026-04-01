package com.example.receipttracker.ui

import androidx.compose.foundation.clickable
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
import com.example.receipttracker.data.AppCurrency.Companion.symbolFromCode
import com.example.receipttracker.data.Receipt
import com.example.receipttracker.data.ReceiptType.Companion.displayNameFromType
import com.example.receipttracker.ui.utils.convertMillisToDate
import java.text.DecimalFormat

@Composable
fun ReceiptList(
    items: List<Receipt>,
    currencyCode: String,
    onReceiptClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { receipt ->
            ReceiptListCard(
                currencyCode = currencyCode,
                receiptUri = receipt.imageUri,
                receiptDate = receipt.date,
                receiptType = receipt.receiptType,
                receiptAmount = receipt.amount,
                receiptNotes = receipt.notes,
                onClick = { onReceiptClick(receipt.receiptId, currencyCode) }
            )
        }
    }
}


@Composable
fun ReceiptListCard(
    currencyCode: String,
    receiptUri: String,
    receiptDate: Long,
    receiptType: String,
    receiptAmount: Double,
    receiptNotes: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currencySymbol = symbolFromCode(currencyCode)
    val dec = DecimalFormat("##,##0.00")
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(
                onClick = { onClick() }
            )
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
                    .weight(0.4f),

            Column(modifier = Modifier.weight(0.6f)) {
                Row {
                    Column(modifier.weight(2f)) {
                        Text(text = "Receipt Date:", style = MaterialTheme.typography.labelMedium)
                    }
                    Column {
                        Text(
                            text = convertMillisToDate(receiptDate),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Row {
                    Column(modifier.weight(2f)) {
                        Text(text = "Type:", style = MaterialTheme.typography.labelMedium)
                    }
                    Column {
                        Text(
                            text = displayNameFromType(receiptType),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Row {
                    Column(modifier.weight(2f)) {
                        Text(text = "Total:", style = MaterialTheme.typography.labelMedium)
                    }
                    Column {
                        Text(
                            text = "$currencySymbol ${dec.format(receiptAmount)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Row { Text(text = "Notes:", style = MaterialTheme.typography.labelMedium) }
                Row {
                    Text(text = receiptNotes, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}