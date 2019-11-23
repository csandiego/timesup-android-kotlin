package com.github.csandiego.timesup.timer

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.TestPresetRepository
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class TimerFragmentUITest {

    private lateinit var device: UiDevice
    private lateinit var app: TestTimesUpApplication
    private lateinit var scenario: FragmentScenario<TimerFragment>
    private lateinit var timer: TestTimer
    private val preset = Preset(id = 1L, name = "2 seconds", seconds = 2)

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        app = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().apply {
            timer = dagger.timer()
        }
        val repository = TestPresetRepository(preset)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TimerViewModel(repository, timer) as T
            }
        }
        val args = Bundle().apply {
            putLong("presetId", preset.id)
        }
        scenario = launchFragmentInContainer(args, R.style.Theme_TimesUp) {
            TimerFragment(viewModelFactory)
        }
    }

    @After
    fun tearDown() {
        if (device.findObject(By.res("com.android.systemui:id/notification_panel")) != null) {
            device.pressBack()
        }
    }
    
    private fun executePendingBindings(fragment: Fragment) {
        DataBindingUtil.getBinding<ViewDataBinding>(fragment.requireView())!!.executePendingBindings()
    }

    private fun findNotification() = device.findObject(
        By.hasChild(
            By.res("android:id/notification_header").hasChild(
                By.res("android:id/app_name_text").text(app.getString(R.string.app_name))
            )
        )
    )

    private fun assertThatNotificationTitle() = assertThat(
        findNotification()?.findObject(By.res("android:id/title"))?.text
    )

    private fun assertThatNotificationText() = assertThat(
        findNotification()?.findObject(By.res("android:id/text"))?.text
    )

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
        onView(withId(R.id.textViewName)).check(matches(withText(preset.name)))
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration))))
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
    fun givenTimerIsInStartedStateWhenOneSecondPassedThenUpdateTimeLeft() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration - 1L))))
    }

    @Test
    fun givenTimerIsInStartedStateWhenExpiredThenOnlyEnableResetButton() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(this@TimerFragmentUITest.preset.duration)
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
                advanceBy(this@TimerFragmentUITest.preset.duration)
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(0L))))
    }

    @Test
    fun givenTimerIsInStartedStateWhenExpiredThenShowNotification() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(this@TimerFragmentUITest.preset.duration)
            }
        }
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(preset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(0L))
    }

    @Test
    fun givenTimerIsInStartedStateWhenFragmentStoppedThenStartForegroundService() {
        scenario.onFragment {
            timer.start()
        }.moveToState(Lifecycle.State.CREATED)
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(preset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(preset.duration))
    }

    @Test
    fun givenTimerIsInStartedStateWhenFragmentStartedThenStopForegroundService() {
        scenario.onFragment {
            timer.start()
        }.moveToState(Lifecycle.State.CREATED).moveToState(Lifecycle.State.RESUMED)
        device.openNotification()
        assertThat(findNotification()).isNull()
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
            .check(matches(withText(DurationFormatter.format(preset.duration))))
    }

    @Test
    fun givenTimerIsInPausedStateWhenFragmentStoppedThenStartForegroundService() {
        val advance = 1L
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(advance)
                pause()
            }
        }.moveToState(Lifecycle.State.CREATED)
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(preset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(preset.duration - advance))
    }

    @Test
    fun givenTimerIsInPausedStateWhenFragmentStartedThenStopForegroundService() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(1L)
                pause()
            }
        }.moveToState(Lifecycle.State.CREATED).moveToState(Lifecycle.State.RESUMED)
        device.openNotification()
        assertThat(findNotification()).isNull()
    }

    @Test
    fun givenTimerIsInFinishedStateWhenResetButtonClickedThenResetTimer() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(this@TimerFragmentUITest.preset.duration)
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
                advanceBy(this@TimerFragmentUITest.preset.duration)
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
                advanceBy(this@TimerFragmentUITest.preset.duration)
                reset()
            }
            executePendingBindings(it)
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration))))
    }

    @Test
    fun givenTimerIsInFinishedStateWhenFragmentStoppedThenStartForegroundService() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(this@TimerFragmentUITest.preset.duration)
            }
        }.moveToState(Lifecycle.State.CREATED)
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(preset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(0L))
    }

    @Ignore
    @Test
    fun givenTimerIsInFinishedStateWhenFragmentStartedThenStopForegroundService() {
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(this@TimerFragmentUITest.preset.duration)
            }
        }.moveToState(Lifecycle.State.CREATED).moveToState(Lifecycle.State.RESUMED)
        device.openNotification()
        assertThat(findNotification()).isNull()
    }
}