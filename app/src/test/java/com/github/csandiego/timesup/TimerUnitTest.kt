package com.github.csandiego.timesup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.timer.Timer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class TimerUnitTest {

    private val preset = Preset(id = 1L, name = "01 seconds", seconds = 1)

    private lateinit var timer: Timer

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val repository = mock(PresetRepository::class.java).apply {
            `when`(getAsLiveData(2)).thenReturn(MutableLiveData(preset))
        }
        timer = Timer(repository).apply {
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

    @Test
    fun whenLoadedThenIsInInitialState() {
        assertThat(timer.state.value).isEqualTo(Timer.State.INITIAL)
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenIsInLoadedState() {
        with(timer) {
            load(2)
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetPreset() {
        with(timer) {
            load(2)
            assertThat(preset.value).isEqualTo(this@TimerUnitTest.preset)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetTimeLeft() {
        with(timer) {
            load(2)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }

    @Test
    fun givenIsInLoadedStateWhenStaredThenIsInStartedState() {
        with(timer) {
            load(2)
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenPausedThenIsInPausedState() {
        with(timer) {
            load(2)
            start()
            pause()
            assertThat(state.value).isEqualTo(Timer.State.PAUSED)
        }
    }

    @Test
    fun givenIsInPausedStateWhenStartedThenIsInStartedState() {
        with(timer) {
            load(2)
            start()
            pause()
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun givenIsInPausedStateWhenResetThenIsInLoadedState() {
        with(timer) {
            load(2)
            start()
            pause()
            reset()
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenIsInFinishedState() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            assertThat(state.value).isEqualTo(Timer.State.FINISHED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenUpdateTimeLeft() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            assertThat(timeLeft.value).isEqualTo("00:00:00")
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenShowNotification() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            assertThat(showNotification.value).isTrue()
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenIsInLoadedState() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            reset()
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenResetTimeLeft() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            reset()
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }
}