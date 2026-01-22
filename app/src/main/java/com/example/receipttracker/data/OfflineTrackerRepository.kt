package com.example.receipttracker.data

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

class OfflineTrackerRepository(private val tripDao: TripDao, private val receiptDao: ReceiptDao) :
    TrackerRepository {
    override fun getTripStream(id: Int): Flow<Trip?> =
        tripDao.getTrip(id)

    override fun getAllTripsByDateAscStream(): Flow<List<Trip>> =
        tripDao.getAllTripsByDateAsc()

    override suspend fun insertTrip(trip: Trip): Long =
        tripDao.insert(trip)

    override suspend fun updateTrip(trip: Trip) =
        tripDao.update(trip)

    override suspend fun deleteTripById(id: Int) =
        tripDao.deleteById(id)

    override suspend fun getTripById(id: Int) =
        tripDao.getTripById(id)

    override fun getReceiptStream(id: Int): Flow<Receipt?> =
        receiptDao.getReceipt(id)

    override suspend fun getReceiptById(id: Int): Receipt? =
        receiptDao.getReceiptById(id)

    override fun getAllReceiptsForTripStream(id: Int): Flow<List<Receipt>> =
        receiptDao.getAllReceiptsForTrip(id)

    override suspend fun insertReceipt(receipt: Receipt) =
        receiptDao.insert(receipt)

    override suspend fun updateReceipt(receipt: Receipt) =
        receiptDao.update(receipt)

    override suspend fun deleteReceiptById(id: Int) =
        receiptDao.deleteById(id)

    override suspend fun getReceiptsForTripForDeletion(id: Int) =
        receiptDao.getReceiptsForTripForDeletion(id)

    @Transaction
    override suspend fun deleteReceiptAndUpdateTripTotal(receiptId: Int, tripId: Int) {
        val receipt = getReceiptById(receiptId)
        if (receipt != null) {
            val trip = getTripById(tripId)
            if (trip != null) {
                val newTotal = (trip.totalAmount - receipt.amount).coerceAtLeast(0.0)
                updateTrip(trip.copy(totalAmount = newTotal))
            }
            deleteReceiptById(receiptId)
        }
    }
}