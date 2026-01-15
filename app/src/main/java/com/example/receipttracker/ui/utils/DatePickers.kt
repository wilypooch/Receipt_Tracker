package com.example.receipttracker.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TripDateRangePicker(state: DateRangePickerState) {
    DateRangePicker(
        state = state,
        title = {
            Text(
                text = "Select trip dates", modifier = Modifier.padding(16.dp)
            )
        }, headline = {
            Row(modifier = Modifier.padding(16.dp)) {
                Box(Modifier.weight(1f)) {
                    (state.selectedStartDateMillis?.let { convertMillisToDate(it) }
                        ?: "Start Date").let { Text(it) }
                }
                Box(Modifier.weight(1f)) {
                    (state.selectedEndDateMillis?.let { convertMillisToDate(it) }
                        ?: "End Date").let { Text(it) }
                }
            }
        },
        showModeToggle = true,
        modifier = Modifier.height(height = 500.dp)
    )
}

@Composable
fun ReceiptDatePicker(state: DatePickerState) {
    DatePicker(
        state = state,
        title = {
            Text(text = "Select Receipt Date", modifier = Modifier.padding(16.dp))
        },
        headline = {
            Row(modifier = Modifier.padding(16.dp)) {
                Box(Modifier.weight(1f)) {
                    (state.selectedDateMillis?.let { convertMillisToDate(it) }
                        ?: "Selected Date").let { Text(it) }
                }
            }
        },
        showModeToggle = true,
        modifier = Modifier.height(height = 500.dp)
    )
}