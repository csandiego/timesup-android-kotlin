package com.github.csandiego.timesup.timer

import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerGivenLoadedWith2SecondsUnitTest : TimerUnitTest() {

    private val testPreset = Preset(id = 1L, name = "02 seconds", seconds = 2)

    @Before
    fun load() {
        timer.load(testPreset)
    }

    @Test
    fun whenStartedThenUpdateTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - 1L)
        }
    }

    @Test
    fun givenIsInStartedStateWhenPausedThenPauseTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
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
    fun givenIsInPausedStateWhenStartedThenUpdateTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
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
    }

    @Test
    fun givenIsInPausedStateWhenResetThenResetTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
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