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
import org.mockito.Mockito

class Timer2SecondUnitTest {

    private val preset = Preset(id = 1L, name = "02 seconds", seconds = 2)

    private lateinit var timer: Timer

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val repository = Mockito.mock(PresetRepository::class.java).apply {
            Mockito.`when`(getAsLiveData(2)).thenReturn(MutableLiveData(preset))
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
    fun givenIsInLoadedStateWhenStaredThenUpdateTimeLeft() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }


    @Test
    fun givenIsInStartedStateWhenPausedThenPauseTimeLeft() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            pause()
            delay(1500L)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }

    @Test
    fun givenIsInPausedStateWhenStartedThenUpdateTimeLeft() = runBlocking {
        with(timer) {
            load(2)
            start()
            pause()
            delay(1500L)
            start()
            delay(1500L)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }

    @Test
    fun givenIsInPausedStateWhenResetThenResetTimeLeft() = runBlocking {
        with(timer) {
            load(2)
            start()
            delay(1500L)
            pause()
            reset()
            assertThat(timeLeft.value).isEqualTo("00:00:02")
        }
    }
}