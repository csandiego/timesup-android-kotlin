package com.github.csandiego.timesup

import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.timer.Timer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerInitialStateUnitTest : TimerUnitTest() {

    private val testPreset = Preset(id = 1L, name = "01 second", seconds = 1)

    @Test
    fun whenLoadedThenIsInInitialState() {
        assertThat(timer.state.value).isEqualTo(Timer.State.INITIAL)
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenIsInLoadedState() {
        with(timer) {
            load(testPreset)
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetPreset() {
        with(timer) {
            load(testPreset)
            assertThat(preset.value).isEqualTo(testPreset)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetTimeLeft() {
        with(timer) {
            load(testPreset)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration)
        }
    }
}