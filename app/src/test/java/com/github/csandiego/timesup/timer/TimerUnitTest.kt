package com.github.csandiego.timesup.timer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.csandiego.timesup.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
abstract class TimerUnitTest {

    protected lateinit var currentTimeProvider: TestCurrentTimeProvider
    protected lateinit var timer: Timer

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        currentTimeProvider = TestCurrentTimeProvider()
        timer = Timer(currentTimeProvider).apply {
            state.observeForever {}
            preset.observeForever {}
            timeLeft.observeForever {}
            showNotification.observeForever {}
        }
    }

    @After
    fun tearDown() {
        timer.clear()
    }
}