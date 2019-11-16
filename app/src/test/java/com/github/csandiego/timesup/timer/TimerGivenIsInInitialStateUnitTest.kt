package com.github.csandiego.timesup.timer

import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerGivenIsInInitialStateUnitTest : TimerUnitTest() {

    private val testPreset = Preset(id = 1L, name = "01 second", seconds = 1)

    @Test
    fun whenLoadedThenIsInLoadedState() {
        with(timer) {
            load(testPreset)
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun whenLoadedThenSetPreset() {
        with(timer) {
            load(testPreset)
            assertThat(preset.value).isEqualTo(testPreset)
        }
    }

    @Test
    fun whenLoadedThenSetTimeLeft() {
        with(timer) {
            load(testPreset)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration)
        }
    }
}