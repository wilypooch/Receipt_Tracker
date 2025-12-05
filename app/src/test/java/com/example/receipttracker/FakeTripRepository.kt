package com.example.receipttracker

import com.example.receipttracker.data.Receipt
import com.example.receipttracker.data.TrackerRepository
import com.example.receipttracker.data.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeTripRepository: TrackerRepository {
    private val trips = mutableListOf<Trip>()
    private val tripsFlow = MutableStateFlow<List<Trip>>(emptyList())


    override fun getTripStream(id: Int): Flow<Trip?> {
        return flow {
            emit(trips.find { it.tripId == id })
        }
    }

    override fun getAllTripsByDateAscStream(): Flow<List<Trip>> {
        return tripsFlow
    }

    override suspend fun insertTrip(trip: Trip) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrip(trip: Trip) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(trip: Trip) {
        TODO("Not yet implemented")
    }

    override fun getReceiptStream(id: Int): Flow<Receipt?> {
        TODO("Not yet implemented")
    }

    override fun getAllReceiptsForTripStream(id: Int): Flow<List<Receipt>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReceipt(receipt: Receipt) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        TODO("Not yet implemented")
    }
}