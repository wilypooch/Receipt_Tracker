package com.example.receipttracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trip: Trip): Long

    @Update
    suspend fun update(trip: Trip)

    @Query("DELETE FROM trip WHERE trip_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM trip WHERE trip_id = :id")
    fun getTrip(id: Int): Flow<Trip?>

    // Non-flow version that is used when deleting receipts
    @Query("SELECT * FROM trip WHERE trip_Id = :id")
    suspend fun getTripById(id: Int): Trip?

    @Query("SELECT * FROM trip WHERE name != '' ORDER BY start_date ASC")
    fun getAllTripsByDateAsc(): Flow<List<Trip>>
}