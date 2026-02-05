package com.example.receipttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Trip::class, Receipt::class], version = 4)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var Instance: TrackerDatabase? = null

        fun getDatabase(context: Context): TrackerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TrackerDatabase::class.java, "tracker_database")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}