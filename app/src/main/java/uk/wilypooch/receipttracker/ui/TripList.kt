package uk.wilypooch.receipttracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import uk.wilypooch.receipttracker.data.AppCurrency.Companion.symbolFromCode
import uk.wilypooch.receipttracker.data.Trip
import uk.wilypooch.receipttracker.ui.utils.convertMillisToDate
import java.text.DecimalFormat

@Composable
fun TripList(
    items: List<Trip>,
    onTripClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            TripListCard(
                tripName = item.name,
                tripStartDate = item.startDate,
                tripTotal = item.totalAmount,
                tripCurrencyCode = item.currencyCode,
                onClick = { onTripClick(item.tripId) }
            )
        }
    }
}

@Composable
fun TripListCard(
    tripName: String,
    tripStartDate: Long,
    tripTotal: Double,
    tripCurrencyCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dec = DecimalFormat("##,##0.00")
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = onClick
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Row {
                Text(tripName, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier.height(8.dp))
            Row {
                Column(modifier.weight(2f)) {
                    Text(text = "Start Date:", style = MaterialTheme.typography.labelLarge)
                }
                Column {
                    Text(
                        text = convertMillisToDate(tripStartDate),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Row {
                Column(modifier.weight(2f)) {
                    Text(text = "Total:", style = MaterialTheme.typography.labelLarge)
                }
                Column {
                    Text(
                        text = "${symbolFromCode(tripCurrencyCode)}${dec.format(tripTotal)}",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
