package com.github.csandiego.timesup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.timer.TestCurrentTimeProvider
import com.github.csandiego.timesup.timer.Timer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class Timer2SecondUnitTest {

    private val dispatcher = TestCoroutineDispatcher()

    private val preset = Preset(id = 1L, name = "02 seconds", seconds = 2)

    private lateinit var currentTimeProvider: TestCurrentTimeProvider
    private lateinit var timer: Timer

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        val repository = mock(PresetRepository::class.java).apply {
            `when`(getAsLiveData(preset.id)).thenReturn(MutableLiveData(preset))
        }
        currentTimeProvider = TestCurrentTimeProvider()
        timer = Timer(repository, currentTimeProvider).apply {
            state.observeForever {}
            preset.observeForever {}
            timeLeft.observeForever {}
            showNotification.observeForever {}
        }
    }

    @After
    fun tearDown() {
        timer.clear()
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun givenIsInLoadedStateWhenStaredThenUpdateTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@Timer2SecondUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
        currentTimeProvider.currentTime = 2001L
    }

    @Test
    fun givenIsInStartedStateWhenPausedThenPauseTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@Timer2SecondUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            pause()
            currentTimeProvider.currentTime = 2001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }

    @Test
    fun givenIsInPausedStateWhenStartedThenUpdateTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@Timer2SecondUnitTest.preset.id)
            start()
            pause()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            start()
            currentTimeProvider.currentTime = 2001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
        currentTimeProvider.currentTime = 3001L
    }

    @Test
    fun givenIsInPausedStateWhenResetThenResetTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@Timer2SecondUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            pause()
            reset()
            assertThat(timeLeft.value).isEqualTo("00:00:02")
        }
    }
}