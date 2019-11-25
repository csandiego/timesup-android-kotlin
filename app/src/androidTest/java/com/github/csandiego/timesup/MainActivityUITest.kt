package com.github.csandiego.timesup

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.*
import com.github.csandiego.timesup.timer.DurationFormatter
import com.github.csandiego.timesup.timer.TestTimer
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityUITest {

    private lateinit var device: UiDevice
    private lateinit var timer: TestTimer
    private val _presets = listOf(
        Preset(id = 1L, name = "2 seconds", seconds = 2),
        Preset(id = 2L, name = "3 seconds", seconds = 3),
        Preset(id = 3L, name = "1 second", seconds = 1)
    )
    private val presets = _presets.sortedBy { it.name }

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        with(ApplicationProvider.getApplicationContext<TestTimesUpApplication>().dagger) {
            dao().insert(_presets)
            timer = timer()
        }
    }

    @After
    fun tearDown() {
        if (findNotificationPanel() != null) {
            device.pressBack()
        }
    }

    @Test
    fun createPreset() {
        val preset = Preset(name = "1 minute", minutes = 1)
        onView(withId(R.id.buttonNew)).perform(click())
        fillUpUsing(preset)
        onView(withText(R.string.button_save)).perform(click())
        onView(isTheRowFor(preset)).check(matches(isDisplayed()))
    }

    @Test
    fun editPreset() {
        val preset = presets[0].copy(name = "Edited Name")
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("menuEdit")).perform(click())
        fillUpUsing(preset)
        onView(withText(R.string.button_save)).perform(click())
        onView(isTheRowFor(presets[0])).check(doesNotExist())
        onView(isTheRowFor(preset)).check(matches(isDisplayed()))
    }

    @Test
    fun deletePresetBySwipingLeft() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft()))
        onView(isTheRowFor(presets[0])).check(doesNotExist())
    }

    @Test
    fun deletePresetBySwipingRight() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))
        onView(isTheRowFor(presets[0])).check(doesNotExist())
    }

    @Test
    fun deletePresetByMenu() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("menuDelete")).perform(click())
        onView(isTheRowFor(presets[0])).check(doesNotExist())
    }

    @Test
    fun runTimer() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        onView(withId(R.id.buttonStart)).perform(click())
        timer.advanceInBackgroundBy(presets[1].duration)
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(0L))))
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(presets[1].name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(0L))
    }

    @Test
    fun runTimerInBackground() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        onView(withId(R.id.buttonStart)).perform(click())
        activityScenarioRule.scenario.moveToState(Lifecycle.State.CREATED)
        timer.advanceInBackgroundBy(presets[1].duration)
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(presets[1].name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(0L))
        device.pressBack()
        activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.textViewTimeLeft))
            .check(matches(withText(DurationFormatter.format(0L))))
    }
}