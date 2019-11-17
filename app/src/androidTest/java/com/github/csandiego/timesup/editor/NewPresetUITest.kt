package com.github.csandiego.timesup.editor

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.assertBound
import com.github.csandiego.timesup.test.fillUpUsing
import com.github.csandiego.timesup.test.isTheRowFor
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewPresetUITest {
    
    private val emptyPreset = Preset()
    private val preset = Preset(name = "1 second", seconds = 1)

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        onView(withId(R.id.buttonNew)).perform(click())
    }

    @Test
    fun whenLoadedThenBindEmptyPreset() {
        assertBound(emptyPreset)
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset)
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset.copy(name = preset.name))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenDisablePositiveButton() {
        fillUpUsing(preset.copy(name = emptyPreset.name))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenEnablePositiveButton() {
        fillUpUsing(preset)
        onView(withText(R.string.button_save)).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenAddToList() {
        with(preset) {
            fillUpUsing(this)
            onView(withText(R.string.button_save)).perform(click())
            onView(isTheRowFor(this)).check(matches(isDisplayed()))
        }
    }
}