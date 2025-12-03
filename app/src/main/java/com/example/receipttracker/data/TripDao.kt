package com.example.receipttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)

    @Query("SELECT * from trip WHERE trip_id = :id")
    fun getTrip(id: Int): Flow<Trip?>

    @Query("SELECT * from trip ORDER BY start_date ASC")
    fun getAllTripsByDateAsc(): Flow<List<Trip>>
}