package com.example.receipttracker

import android.app.Application
import com.example.receipttracker.di.AppContainer
import com.example.receipttracker.di.AppDataContainer

class ReceiptTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}