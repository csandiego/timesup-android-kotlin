package com.github.csandiego.timesup

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
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerUITest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(presets)
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(2),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click())
            )
    }

    @Test
    fun whenLoadedThenDisplayNameAndTimeLeft() {
        onView(withId(R.id.textViewName)).check(matches(withText(presetsSortedByName[2].name)))
        onView(withId(R.id.textViewTimeLeft))
            .check(
                matches(
                    withText(
                        String.format(
                            "%02d:%02d:%02d",
                            presetsSortedByName[2].hours,
                            presetsSortedByName[2].minutes,
                            presetsSortedByName[2].seconds
                        )
                    )
                )
            )
    }

    @Test
    fun whenLoadedThenOnlyEnableStartButton() {
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenStartedThenOnlyEnablePauseButton() {
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
    fun givenPausedWhenResetThenOnlyEnableStartButton() {
        onView(withId(R.id.buttonStart)).perform(click())
        onView(withId(R.id.buttonPause)).perform(click())
        onView(withId(R.id.buttonReset)).perform(click())
        onView(withId(R.id.buttonStart)).check(matches(isEnabled()))
        onView(withId(R.id.buttonPause)).check(matches(not(isEnabled())))
        onView(withId(R.id.buttonReset)).check(matches(not(isEnabled())))
    }

    @Test
    fun givenStartedWhenOneSecondPassedThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        delay(1000L)
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText("00:00:01")))
    }

    @Test
    fun givenPausedWhenResumedThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        onView(withId(R.id.buttonPause)).perform(click())
        delay(1000L)
        onView(withId(R.id.buttonStart)).perform(click())
        delay(1000L)
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText("00:00:01")))
    }

    @Test
    fun givenStartedWhenResetThenUpdateTimeLeft() = runBlocking<Unit> {
        onView(withId(R.id.buttonStart)).perform(click())
        delay(1000L)
        onView(withId(R.id.buttonPause)).perform(click())
        onView(withId(R.id.buttonReset)).perform(click())
        onView(withId(R.id.textViewTimeLeft))
            .check(
                matches(
                    withText(
                        String.format(
                            "%02d:%02d:%02d",
                            presetsSortedByName[2].hours,
                            presetsSortedByName[2].minutes,
                            presetsSortedByName[2].seconds
                        )
                    )
                )
            )
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
        onView(withId(R.id.textViewTimeLeft)).check(matches(withText("00:00:00")))
    }
}