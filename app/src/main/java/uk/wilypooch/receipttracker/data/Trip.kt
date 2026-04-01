package uk.wilypooch.receipttracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trip_id")
    val tripId: Int = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "start_date")
    val startDate: Long = 0,
    @ColumnInfo(name = "end_date")
    val endDate: Long = 0,
    @ColumnInfo(name = "currency")
    val currencyCode: String = "GBP",
    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,
)