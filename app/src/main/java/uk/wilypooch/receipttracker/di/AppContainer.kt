package uk.wilypooch.receipttracker.di

import android.content.Context
import uk.wilypooch.receipttracker.data.OfflineTrackerRepository
import uk.wilypooch.receipttracker.data.TrackerDatabase
import uk.wilypooch.receipttracker.data.TrackerRepository

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