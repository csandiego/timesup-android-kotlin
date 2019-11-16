package com.github.csandiego.timesup.timer

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerUITest {
    
    private val preset = Preset(name = "2 seconds", seconds = 2)

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(preset)
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
    }

    @Test
    fun whenLoadedThenDisplayNameAndTimeLeft() {
        onView(withId(R.id.textViewName)).check(matches(withText(preset.name)))
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration))))
    }

    @Test
    fun whenLoadedThenOnlyEnableStartButton() {
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenLoadedwhenStartedThenOnlyEnablePauseButton() {
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
    fun givenStartedWhenOneSecondPassedThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        delay(1000L)
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration - 1L))))
    }

    @Test
    fun givenStartedWhenExpiredThenOnlyEnableResetButton() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        delay(2000L)
        onView(withId(R.id.buttonStart)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(isEnabled()))
    }

    @Test
    fun givenStartedWhenExpiredThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        delay(2000L)
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
    fun givenPausedWhenStartedThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        onView(withId(R.id.buttonPause)).perform(click())
        delay(1000L)
        onView(withId(R.id.buttonStart)).perform(click())
        delay(1000L)
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration - 1L))))
    }

    @Test
    fun givenPausedWhenResetThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        delay(1000L)
        onView(withId(R.id.buttonPause)).perform(click())
        onView(withId(R.id.buttonReset)).perform(click())
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(preset.duration))))
    }
}