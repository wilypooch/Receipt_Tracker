package com.example.receipttracker.data

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
    val tripId: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "image_uri")
    val imageUri: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "notes")
    val notes: String,
)