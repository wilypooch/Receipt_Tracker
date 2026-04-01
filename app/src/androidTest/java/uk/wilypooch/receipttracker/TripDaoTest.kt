package uk.wilypooch.receipttracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import uk.wilypooch.receipttracker.data.TrackerDatabase
import uk.wilypooch.receipttracker.data.Trip
import uk.wilypooch.receipttracker.data.TripDao
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TripDaoTest {
    private lateinit var tripDao: TripDao
    private lateinit var db: TrackerDatabase

    private var trip1 = Trip(
        name = "Defcon",
        startDate = 1711965600000,
        endDate = 1711965600000,
        totalAmount = 300.00
    )

    private var trip2 = Trip(
        name = "SANS",
        startDate = 1711965600000,
        endDate = 1711965600000,
        totalAmount = 400.00
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TrackerDatabase::class.java)
            .allowMainThreadQueries().build()
        tripDao = db.tripDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTrip_AddsTripToDb() = runBlocking {
        tripDao.insert(trip1)
        val allTrips = tripDao.getAllTripsByDateDesc().first()
        assertEquals("Defcon", allTrips[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun getTrip_RetrievesCorrectTripById() = runBlocking {
        tripDao.insert(trip1)
        tripDao.insert(trip2)
        val allTrips = tripDao.getAllTripsByDateDesc().first()
        val idToFind = allTrips.first { it.name == "SANS" }.tripId
        val loadedTrip = tripDao.getTrip(idToFind).filterNotNull().first()
        assertEquals(2, allTrips.size)
        assertEquals("SANS", loadedTrip.name)
        assertEquals(1711965600000, loadedTrip.startDate)
        assertEquals(idToFind, loadedTrip.tripId)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTrip_RemovesItemFromDb() = runBlocking {
        tripDao.insert(trip2)
        var allTrips = tripDao.getAllTripsByDateDesc().first()
        assertEquals(1, allTrips.size)
        val tripToDelete = allTrips[0]
        tripDao.deleteById(tripToDelete.tripId)
        allTrips = tripDao.getAllTripsByDateDesc().first()
        assertTrue(allTrips.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun updateTrip_UpdatesValuesInDb() = runBlocking {
        tripDao.insert(trip1)
        val savedTrip = tripDao.getAllTripsByDateDesc().first()[0]
        val modifiedTrip = savedTrip.copy(name = "Cancelled")
        tripDao.update(modifiedTrip)
        val loadedTrip = tripDao.getTrip(savedTrip.tripId).filterNotNull().first()
        assertEquals("Cancelled", loadedTrip.name)
        assertEquals(1711965600000, loadedTrip.startDate)
    }
}