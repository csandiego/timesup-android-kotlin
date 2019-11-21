package com.github.csandiego.timesup.timer

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.TestPresetRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

class TimerFragmentUITest {

    private lateinit var scenario: FragmentScenario<TimerFragment>
    private lateinit var timer: ManualTimer
    private val preset = Preset(id = 1L, name = "2 seconds", seconds = 2)

    @Before
    fun setUp() = runBlocking {
        val repository = TestPresetRepository(preset)
        timer = ManualTimer()
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

    @Test
    fun givenValidPresetIdWhenLoadedThenOnlyEnableStartButton() {
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenValidPresetIdWhenLoadedThenDisplayNameAndTimeLeft() {
        onView(withId(R.id.textViewName)).check(matches(withText(preset.name)))
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration))))
    }

    @Test
    fun givenLoadedWhenStartedThenOnlyEnablePauseButton() {
        onView(withId(R.id.buttonStart)).perform(click())
        onView(withId(R.id.buttonStart)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonPause)).check(matches(isEnabled()))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenStartedWhenPausedThenOnlyDisablePauseButton() {
        onView(withId(R.id.buttonStart)).perform(click())
        onView(withId(R.id.buttonPause)).perform(click())
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(isEnabled()))
    }

    @Test
    fun givenStartedWhenOneSecondPassedThenUpdateTimeLeft() {
        onView(withId(R.id.buttonStart)).perform(click())
        scenario.onFragment {
            timer.advanceBy(1L)
            DataBindingUtil.getBinding<ViewDataBinding>(it.requireView())!!.executePendingBindings()
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration - 1L))))
    }

    @Test
    fun givenStartedWhenExpiredThenOnlyEnableResetButton() {
        onView(withId(R.id.buttonStart)).perform(click())
        scenario.onFragment {
            timer.advanceBy(preset.duration)
            DataBindingUtil.getBinding<ViewDataBinding>(it.requireView())!!.executePendingBindings()
        }
        onView(withId(R.id.buttonStart)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(isEnabled()))
    }

    @Test
    fun givenStartedWhenExpiredThenUpdateTimeLeft() {
        onView(withId(R.id.buttonStart)).perform(click())
        scenario.onFragment {
            timer.advanceBy(preset.duration)
            DataBindingUtil.getBinding<ViewDataBinding>(it.requireView())!!.executePendingBindings()
        }
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(0L))))
    }

    @Test
    fun givenPausedWhenResetThenOnlyEnableStartButton() {
        onView(withId(R.id.buttonStart)).perform(click())
        onView(withId(R.id.buttonPause)).perform(click())
        onView(withId(R.id.buttonReset)).perform(click())
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenPausedWhenResetThenUpdateTimeLeft() {
        onView(withId(R.id.buttonStart)).perform(click())
        scenario.onFragment {
            timer.advanceBy(1L)
            DataBindingUtil.getBinding<ViewDataBinding>(it.requireView())!!.executePendingBindings()
        }
        onView(withId(R.id.buttonPause)).perform(click())
        onView(withId(R.id.buttonReset)).perform(click())
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration))))
    }
}