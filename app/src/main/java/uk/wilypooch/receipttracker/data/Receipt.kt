package uk.wilypooch.receipttracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipt",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "receipt_id")
    val receiptId: Int = 0,
    @ColumnInfo(name = "trip_id")
    val tripId: Int = 0,
    @ColumnInfo(name = "date")
    val date: Long = 0,
    @ColumnInfo(name = "image_uri")
    val imageUri: String = "",
    @ColumnInfo(name = "receipt_type")
    val receiptType: String = "FoodAndDrink",
    @ColumnInfo(name = "amount")
    val amount: Double = 0.0,
    @ColumnInfo(name = "notes")
    val notes: String = "",
)