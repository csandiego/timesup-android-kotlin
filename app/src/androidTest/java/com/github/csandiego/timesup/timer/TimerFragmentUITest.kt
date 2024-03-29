package com.github.csandiego.timesup.timer

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.TestPresetRepository
import com.github.csandiego.timesup.test.assertThatNotificationText
import com.github.csandiego.timesup.test.assertThatNotificationTitle
import com.github.csandiego.timesup.test.findNotificationPanel
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class TimerFragmentUITest {

    private lateinit var device: UiDevice
    private lateinit var scenario: FragmentScenario<TimerFragment>
    private lateinit var timer: TestTimer
    private val testPreset = Preset(id = 1L, name = "2 seconds", seconds = 2)

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val repository = TestPresetRepository(testPreset)
        timer = TestTimer()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimerViewModel(repository, timer) as T
            }
        }
        val args = Bundle().apply {
            putLong("presetId", testPreset.id)
        }
        scenario = launchFragmentInContainer(args, R.style.Theme_TimesUp) {
            TimerFragment(viewModelFactory)
        }
    }

    @After
    fun tearDown() {
        if (findNotificationPanel() != null) {
            device.pressBack()
        }
    }
    
    private fun executePendingBindings(fragment: Fragment) {
        DataBindingUtil.getBinding<ViewDataBinding>(fragment.requireView())!!.executePendingBindings()
    }

    @Test
    fun givenTimerIsInInitialStateWhenFragmentCreatedThenLoadTimer() {
        scenario.onFragment {
            assertThat(timer.state.value).isEqualTo(Timer.State.LOADED)
        }
    }

    @Test
    fun givenTimerIsInInitialStateWhenTimerLoadedThenOnlyEnableStartButton() {
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenTimerIsInInitialStateWhenTimerLoadedThenDisplayNameAndTimeLeft() {
        onView(withId(R.id.textViewName)).check(matches(withText(testPreset.name)))
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(testPreset.duration))))
    }

    @Test
    fun givenTimerIsInLoadedStateWhenStartButtonClickedThenStartTimer() {
        onView(withId(R.id.buttonStart)).perform(click())
        assertThat(timer.state.value).isEqualTo(Timer.State.STARTED)
    }

    @Test
    fun givenTimerIsInLoadedStateWhenTimerStartedThenOnlyEnablePauseButton() {
        scenario.onFragment {
            timer.start()
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonPause)).check(matches(isEnabled()))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenTimerIsInStartedStateWhenOneSecondPassedThenUpdateTimeLeft() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(testPreset.duration - 1L))))
    }

    @Test
    fun givenTimerIsInStartedStateWhenPauseButtonClickedThenPauseTimer() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonPause)).perform(click())
        assertThat(timer.state.value).isEqualTo(Timer.State.PAUSED)
    }

    @Test
    fun givenTimerIsInStartedStateWhenTimerPausedThenOnlyDisablePauseButton() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(isEnabled()))
    }

    @Test
    fun givenTimerIsInStartedStateWhenExpiredThenOnlyEnableResetButton() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(testPreset.duration)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(isEnabled()))
    }

    @Test
    fun givenTimerIsInStartedStateWhenExpiredThenUpdateTimeLeft() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(testPreset.duration)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(0L))))
    }

    @Test
    @Ignore("Foreground service notification UI changed in Android 13")
    fun givenTimerIsInStartedStateWhenExpiredThenShowNotification() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(testPreset.duration)
            }
        }
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(testPreset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(0L))
    }

    @Test
    fun givenTimerIsInPausedStateWhenStartButtonClickedThenStartTimer() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).perform(click())
        assertThat(timer.state.value).isEqualTo(Timer.State.STARTED)
    }

    @Test
    fun givenTimerIsInPausedStateWhenTimerStartedThenOnlyEnablePauseButton() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
                start()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonPause)).check(matches(isEnabled()))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenTimerIsInPausedStateWhenResetButtonClickedThenResetTimer() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonReset)).perform(click())
        assertThat(timer.state.value).isEqualTo(Timer.State.LOADED)
    }

    @Test
    fun givenTimerIsInPausedStateWhenTimerResetThenOnlyEnableStartButton() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
                reset()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenTimerIsInPausedStateWhenTimerResetThenUpdateTimeLeft() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
                reset()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(testPreset.duration))))
    }

    @Test
    fun givenTimerIsInFinishedStateWhenResetButtonClickedThenResetTimer() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(testPreset.duration)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonReset)).perform(click())
        assertThat(timer.state.value).isEqualTo(Timer.State.LOADED)
    }

    @Test
    fun givenTimerIsInFinishedStateWhenTimerResetThenOnlyEnableStartButton() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(testPreset.duration)
                reset()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenTimerIsInFinishedStateWhenTimerResetThenUpdateTimeLeft() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(testPreset.duration)
                reset()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(testPreset.duration))))
    }
}