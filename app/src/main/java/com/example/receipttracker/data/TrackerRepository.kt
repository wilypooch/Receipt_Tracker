package com.example.receipttracker.data

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

interface TrackerRepository {
    fun getTripStream(id: Int): Flow<Trip?>
    fun getAllTripsByDateDescStream(): Flow<List<Trip>>
    suspend fun insertTrip(trip: Trip): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTripById(id: Int)
    suspend fun getTripById(id: Int): Trip?

    fun getReceiptStream(id: Int): Flow<Receipt?>
    suspend fun getReceiptById(id: Int): Receipt?
    fun getAllReceiptsForTripStream(id: Int): Flow<List<Receipt>>
    suspend fun insertReceipt(receipt: Receipt)
    suspend fun updateReceipt(receipt: Receipt)
    suspend fun deleteReceiptById(id: Int)
    suspend fun getReceiptsForTripForDeletion(id: Int): List<Receipt>
    @Transaction
    suspend fun updateReceiptAndUpdateTripTotal(updatedReceipt: Receipt, oldAmount: Double)
    @Transaction
    suspend fun deleteReceiptAndUpdateTripTotal(receiptId: Int, tripId: Int)
}