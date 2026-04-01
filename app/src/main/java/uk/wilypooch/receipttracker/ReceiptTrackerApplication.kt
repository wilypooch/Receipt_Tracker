package uk.wilypooch.receipttracker

import android.app.Application
import uk.wilypooch.receipttracker.di.AppContainer
import uk.wilypooch.receipttracker.di.AppDataContainer

class ReceiptTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}