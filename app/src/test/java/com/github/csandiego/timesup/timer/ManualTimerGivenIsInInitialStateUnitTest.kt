package com.github.csandiego.timesup.timer

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ManualTimerGivenIsInInitialStateUnitTest : ManualTimerUnitTest() {

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