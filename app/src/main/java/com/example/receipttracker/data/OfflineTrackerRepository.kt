package com.example.receipttracker.data

import kotlinx.coroutines.flow.Flow

class OfflineTrackerRepository(private val tripDao: TripDao, private val receiptDao: ReceiptDao) :
    TrackerRepository {
    override fun getTripStream(id: Int): Flow<Trip?> =
        tripDao.getTrip(id)

    override fun getAllTripsByDateAscStream(): Flow<List<Trip>> =
        tripDao.getAllTripsByDateAsc()

    override suspend fun insertTrip(trip: Trip) =
        tripDao.insert(trip)

    override suspend fun updateTrip(trip: Trip) =
        tripDao.update(trip)

    override suspend fun deleteTrip(trip: Trip) =
        tripDao.delete(trip)

    override fun getReceiptStream(id: Int): Flow<Receipt?> =
        receiptDao.getReceipt(id)

    override fun getAllReceiptsForTripStream(id: Int): Flow<List<Receipt>> =
        receiptDao.getAllReceiptsForTrip(id)

    override suspend fun insertReceipt(receipt: Receipt) =
        receiptDao.insert(receipt)

    override suspend fun updateReceipt(receipt: Receipt) =
        receiptDao.update(receipt)

    override suspend fun deleteReceipt(receipt: Receipt) =
        receiptDao.delete(receipt)
}