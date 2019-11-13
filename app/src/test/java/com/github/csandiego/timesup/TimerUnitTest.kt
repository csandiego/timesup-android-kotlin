package com.github.csandiego.timesup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.timer.CurrentTimeProvider
import com.github.csandiego.timesup.timer.Timer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class TimerUnitTest {

    private val dispatcher = TestCoroutineDispatcher()

    private val preset = Preset(id = 1L, name = "01 seconds", seconds = 1)

    private lateinit var timer: Timer

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        val repository = mock(PresetRepository::class.java)
        val currentTimeProvider = mock(CurrentTimeProvider::class.java)
        timer = Timer(repository, currentTimeProvider).apply {
            state.observeForever {}
            preset.observeForever {}
            timeLeft.observeForever {}
            showNotification.observeForever {}
        }
    }

    @After
    fun tearDown() {
        timer.clear()
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun whenLoadedThenIsInInitialState() {
        assertThat(timer.state.value).isEqualTo(Timer.State.INITIAL)
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenIsInLoadedState() {
        with(timer) {
            load(this@TimerUnitTest.preset)
            assertThat(state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetPreset() {
        with(timer) {
            load(this@TimerUnitTest.preset)
            assertThat(preset.value).isEqualTo(this@TimerUnitTest.preset)
        }
    }

    @Test
    fun givenIsInInitialStateWhenLoadedThenSetTimeLeft() {
        with(timer) {
            load(this@TimerUnitTest.preset)
            assertThat(timeLeft.value).isEqualTo("00:00:01")
        }
    }
}