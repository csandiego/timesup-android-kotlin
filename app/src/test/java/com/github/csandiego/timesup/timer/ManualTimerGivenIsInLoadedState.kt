package com.github.csandiego.timesup.timer

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ManualTimerGivenIsInLoadedState : ManualTimerUnitTest() {

    @Before
    fun load() {
        timer.load(testPreset)
    }

    @Test
    fun whenStartedThenIsInStartedState() {
        with(timer) {
            start()
            assertThat(state.value).isEqualTo(Timer.State.STARTED)
        }
    }

    @Test
    fun whenStartedThenUpdateTimeLeft() {
        with(timer) {
            val advance = 1L
            start()
            advanceBy(advance)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - advance)
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
    fun givenIsInStartedStateWhenFinishedThenIsInFinishedState() {
        with(timer) {
            start()
            advanceBy(testPreset.duration)
            assertThat(state.value).isEqualTo(Timer.State.FINISHED)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenUpdateTimeLeft() {
        with(timer) {
            start()
            advanceBy(testPreset.duration)
            assertThat(timeLeft.value).isEqualTo(0L)
        }
    }

    @Test
    fun givenIsInStartedStateWhenFinishedThenShowNotification() {
        with(timer) {
            start()
            advanceBy(testPreset.duration)
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
    fun givenIsInPausedStateWhenResetThenResetTimeLeft() {
        with(timer) {
            start()
            advanceBy(1L)
            pause()
            reset()
            assertThat(timeLeft.value).isEqualTo(testPreset.duration)
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenIsInLoadedState() {
        with(timer) {
            start()
            advanceBy(testPreset.duration)
            reset()
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInFinishedStateWhenResetThenResetTimeLeft() {
        with(timer) {
            start()
            advanceBy(testPreset.duration)
            reset()
            assertThat(timeLeft.value).isEqualTo(testPreset.duration)
        }
    }
}