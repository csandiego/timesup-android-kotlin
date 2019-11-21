package com.github.csandiego.timesup.timer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.csandiego.timesup.data.Preset
import org.junit.Before
import org.junit.Rule

abstract class ManualTimerUnitTest {

    protected lateinit var timer: ManualTimer
    protected val testPreset = Preset(id = 1L, name = "2 seconds", seconds = 2)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        timer = ManualTimer().apply {
            state.observeForever {}
            preset.observeForever {}
            timeLeft.observeForever {}
            showNotification.observeForever {}
        }
    }
}