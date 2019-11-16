package com.github.csandiego.timesup.timer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class TimerUnitTest {

    protected lateinit var currentTimeProvider: TestCurrentTimeProvider
    protected lateinit var timer: Timer

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    protected val dispatcher = TestCoroutineDispatcher()

    @Before
    open fun setUp() {
        Dispatchers.setMain(dispatcher)
        currentTimeProvider = TestCurrentTimeProvider()
        timer = Timer(currentTimeProvider).apply {
            state.observeForever {}
            preset.observeForever {}
            timeLeft.observeForever {}
            showNotification.observeForever {}
        }
    }

    @After
    open fun tearDown() {
        timer.clear()
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }
}