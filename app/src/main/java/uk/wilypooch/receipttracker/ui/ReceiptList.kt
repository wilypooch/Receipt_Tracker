package uk.wilypooch.receipttracker.ui

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
import uk.wilypooch.receipttracker.data.AppCurrency.Companion.symbolFromCode
import uk.wilypooch.receipttracker.data.Receipt
import uk.wilypooch.receipttracker.data.ReceiptType.Companion.displayNameFromType
import uk.wilypooch.receipttracker.ui.utils.convertMillisToDate
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
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = receiptUri,
                contentDescription = "Receipt Preview",
                modifier = Modifier
                    .height(150.dp)
                    .weight(0.4f),
            )

            Column(modifier = Modifier.weight(0.6f)) {
                Text(
                    text = "${convertMillisToDate(receiptDate)} • ${displayNameFromType(receiptType)}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "$currencySymbol${dec.format(receiptAmount)}",
                    style = MaterialTheme.typography.titleSmall
                )
                if (receiptNotes.isNotEmpty()) {
                    Text(text = receiptNotes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}