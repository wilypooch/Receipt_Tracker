package uk.wilypooch.receipttracker.ui.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}