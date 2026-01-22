package com.example.receipttracker.data

import androidx.room.Dao
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

    @Query("DELETE FROM receipt WHERE receipt_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM receipt WHERE receipt_id = :id")
    fun getReceipt(id: Int): Flow<Receipt?>

    // Non-flow version that is used when deleting receipts
    @Query("SELECT * FROM receipt WHERE receipt_Id = :id")
    suspend fun getReceiptById(id: Int): Receipt?

    @Query("SELECT * FROM receipt WHERE trip_id = :id ORDER BY receipt_id")
    fun getAllReceiptsForTrip(id: Int): Flow<List<Receipt>>

    // Non-flow version for file clean up when Trip is deleted
    @Query("SELECT * FROM receipt WHERE trip_id = :id")
    suspend fun getReceiptsForTripForDeletion(id: Int): List<Receipt>
}