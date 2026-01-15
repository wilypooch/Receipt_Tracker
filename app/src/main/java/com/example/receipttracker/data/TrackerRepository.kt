package com.example.receipttracker.data

import kotlinx.coroutines.flow.Flow

interface TrackerRepository {
    fun getTripStream(id: Int): Flow<Trip?>
    fun getAllTripsByDateAscStream(): Flow<List<Trip>>
    suspend fun insertTrip(trip: Trip): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTripById(id: Int)

    fun getReceiptStream(id: Int): Flow<Receipt?>
    fun getAllReceiptsForTripStream(id: Int): Flow<List<Receipt>>
    suspend fun insertReceipt(receipt: Receipt)
    suspend fun updateReceipt(receipt: Receipt)
    suspend fun deleteReceiptById(id: Int)
    suspend fun getReceiptsForTripForDeletion(id: Int): List<Receipt>
}