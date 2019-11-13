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
class TimerUnitTest {

    private val dispatcher = TestCoroutineDispatcher()

    private val preset = Preset(id = 1L, name = "01 seconds", seconds = 1)

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
    fun whenLoadedThenIsInInitialState() {
        assertThat(timer.state.value).isEqualTo(Timer.State.INITIAL)
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenIsInLoadedState() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetPreset() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            assertThat(preset.value).isEqualTo(this@TimerUnitTest.preset)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetTimeLeft() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }

    @Test
    fun givenIsInLoadedStateWhenStaredThenIsInStartedState() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenPausedThenIsInPausedState() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            pause()
            assertThat(state.value).isEqualTo(Timer.State.PAUSED)
        }
    }

    @Test
    fun givenIsInPausedStateWhenStartedThenIsInStartedState() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            pause()
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun givenIsInPausedStateWhenResetThenIsInLoadedState() {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            pause()
            reset()
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenIsInFinishedState() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(state.value).isEqualTo(Timer.State.FINISHED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenUpdateTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo("00:00:00")
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenShowNotification() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(showNotification.value).isTrue()
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenIsInLoadedState() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            reset()
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenResetTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            load(this@TimerUnitTest.preset.id)
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            reset()
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }
}