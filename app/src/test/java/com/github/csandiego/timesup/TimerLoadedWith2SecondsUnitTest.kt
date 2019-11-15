package com.github.csandiego.timesup

import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class Timer2SecondUnitTest : TimerUnitTest() {

    private val testPreset = Preset(id = 1L, name = "02 seconds", seconds = 2)

    @Before
    override fun setUp() {
        super.setUp()
        timer.load(testPreset)
    }

    @Test
    fun givenIsInLoadedStateWhenStartedThenUpdateTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - 1L)
        }
        currentTimeProvider.currentTime = 2001L
    }

    @Test
    fun givenIsInStartedStateWhenPausedThenPauseTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            pause()
            currentTimeProvider.currentTime = 2001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - 1L)
        }
    }

    @Test
    fun givenIsInPausedStateWhenStartedThenUpdateTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            pause()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            start()
            currentTimeProvider.currentTime = 2001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - 1L)
        }
        currentTimeProvider.currentTime = 3001L
    }

    @Test
    fun givenIsInPausedStateWhenResetThenResetTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            pause()
            reset()
            assertThat(timeLeft.value).isEqualTo(testPreset.duration)
        }
    }
}