package com.example.receipttracker.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.receipttracker.data.Trip

private val tripsForPreview = listOf(
    Trip(23, "SANSHOME", "01/02/23", endDate = "08/03/1993", 400.00),
    Trip(24, "Defcon33HOME", "05/08/25", endDate = "08/03/1993", 20.00),
    Trip(25, "Work TripHOIME", "01/02/25", endDate = "08/03/1993", 4888.86)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onViewTripClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsState()

    TripList(
        items = uiState.trips,
        onTripClick = onViewTripClick,
        onDeleteTrip = viewModel::deleteTrip,
        modifier = modifier
    )

}


//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    ReceiptTrackerTheme {
//        HomeScreen(
//        )
//    }
//}