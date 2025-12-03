package com.example.receipttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(receipt: Receipt)

    @Update
    suspend fun update(receipt: Receipt)

    @Delete
    suspend fun delete(receipt: Receipt)

    @Query("SELECT * FROM receipt WHERE receipt_id = :id")
    fun getReceipt(id: Int): Flow<Receipt?>

    @Query("SELECT * FROM receipt WHERE trip_id = :id ORDER BY receipt_id")
    fun getAllReceiptsForTrip(id: Int): Flow<List<Receipt>>
}