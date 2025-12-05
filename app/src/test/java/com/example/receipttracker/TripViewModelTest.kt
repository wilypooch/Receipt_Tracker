package com.example.receipttracker

import com.example.receipttracker.ui.TripViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

class TripViewModelTest {

    // 1. System Under Test
    private lateinit var viewModel: TripViewModel

    // 2. Dependencies
    private lateinit var repository: FakeTripRepository

    // 3. Coroutine Test Rule
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for the test
        repository = FakeTripRepository()
        viewModel = TripViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher after the test
    }
}