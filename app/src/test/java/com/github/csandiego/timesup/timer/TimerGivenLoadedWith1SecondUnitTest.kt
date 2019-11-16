package com.github.csandiego.timesup.timer

import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerGivenLoadedWith1SecondUnitTest : TimerUnitTest() {

    private val testPreset = Preset(id = 1L, name = "01 second", seconds = 1)

    @Before
    override fun setUp() {
        super.setUp()
        timer.load(testPreset)
    }

    @Test
    fun whenStaredThenIsInStartedState() {
        with(timer) {
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenPausedThenIsInPausedState() {
        with(timer) {
            start()
            pause()
            assertThat(state.value).isEqualTo(Timer.State.PAUSED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenIsInFinishedState() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(state.value).isEqualTo(Timer.State.FINISHED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenUpdateTimeLeft() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(timeLeft.value).isEqualTo(0L)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenShowNotification() = dispatcher.runBlockingTest {
        with(timer) {
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            assertThat(showNotification.value).isTrue()
        }
    }

    @Test
    fun givenIsInPausedStateWhenStartedThenIsInStartedState() {
        with(timer) {
            start()
            pause()
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun givenIsInPausedStateWhenResetThenIsInLoadedState() {
        with(timer) {
            start()
            pause()
            reset()
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenIsInLoadedState() = dispatcher.runBlockingTest {
        with(timer) {
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
            start()
            currentTimeProvider.currentTime = 1001L
            advanceTimeBy(1000L)
            reset()
            assertThat(timeLeft.value).isEqualTo(testPreset.duration)
        }
    }
}