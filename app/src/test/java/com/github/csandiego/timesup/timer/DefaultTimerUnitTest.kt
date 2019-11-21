package com.github.csandiego.timesup.timer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultTimerUnitTest {

    private lateinit var currentTimeProvider: TestCurrentTimeProvider
    private lateinit var timer: DefaultTimer
    private val testPreset = Preset(id = 1L, name = "3 seconds", seconds = 3)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        currentTimeProvider = TestCurrentTimeProvider()
        timer = DefaultTimer(currentTimeProvider).apply {
            state.observeForever {}
            preset.observeForever {}
            timeLeft.observeForever {}
            showNotification.observeForever {}
            load(testPreset)
        }
    }

    @After
    fun tearDown() {
        timer.clear()
    }

    @Test
    fun givenStartedWhenOneSecondPassesThenUpdateTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
        with(timer) {
            val advance = 1L
            val advanceInMillis = advance * 1000L
            start()
            currentTimeProvider.currentTime = advanceInMillis
            advanceTimeBy(advanceInMillis)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - advance)
        }
    }

    @Test
    fun givenStartedWhenFinishedThenIsInFinishedState() = mainDispatcherRule.dispatcher.runBlockingTest {
        with(timer) {
            val advance = 3L
            val advanceInMillis = advance * 1000L
            start()
            currentTimeProvider.currentTime = advanceInMillis
            advanceTimeBy(advanceInMillis)
            assertThat(state.value).isEqualTo(Timer.State.FINISHED)
        }
    }

    @Test
    fun givenStartedWhenFinishedThenUpdateTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
        with(timer) {
            val advance = 3L
            val advanceInMillis = advance * 1000L
            start()
            currentTimeProvider.currentTime = advanceInMillis
            advanceTimeBy(advanceInMillis)
            assertThat(timeLeft.value).isEqualTo(0L)
        }
    }

    @Test
    fun givenStartedWhenPausedThenDoNotUpdateTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
        with(timer) {
            val advance = 1L
            val advanceInMillis = advance * 1000L
            start()
            currentTimeProvider.currentTime = advanceInMillis
            advanceTimeBy(advanceInMillis)
            pause()
            currentTimeProvider.currentTime = advanceInMillis * 2L
            advanceTimeBy(advanceInMillis)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - advance)
        }
    }

    @Test
    fun givenPausedWhenStartedThenUpdateTimeLeft() = mainDispatcherRule.dispatcher.runBlockingTest {
        with(timer) {
            val advance = 1L
            val advanceInMillis = advance * 1000L
            start()
            currentTimeProvider.currentTime = advanceInMillis
            advanceTimeBy(advanceInMillis)
            pause()
            currentTimeProvider.currentTime = advanceInMillis * 2L
            advanceTimeBy(advanceInMillis)
            start()
            currentTimeProvider.currentTime = advanceInMillis * 3L
            advanceTimeBy(advanceInMillis)
            assertThat(timeLeft.value).isEqualTo(testPreset.duration - advance * 2L)
        }
    }
}