package com.example.receipttracker.di

import android.content.Context
import com.example.receipttracker.data.OfflineTrackerRepository
import com.example.receipttracker.data.TrackerDatabase
import com.example.receipttracker.data.TrackerRepository

interface AppContainer {
    val trackerRepository: TrackerRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val trackerRepository: TrackerRepository by lazy {
        OfflineTrackerRepository(
            TrackerDatabase.getDatabase(context).tripDao(),
            TrackerDatabase.getDatabase(context).receiptDao()
        )
    }
}