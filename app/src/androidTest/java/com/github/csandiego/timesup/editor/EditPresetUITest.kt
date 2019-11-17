package com.github.csandiego.timesup.editor

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.assertBound
import com.github.csandiego.timesup.test.fillUpUsing
import com.github.csandiego.timesup.test.isTheRowFor
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditPresetUITest {

    private val emptyPreset = Preset()
    private val preset = Preset(name = "1 second", seconds = 1)
    private val editedName = "Edited Name"

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>().database.presetDao()
            .insert(preset)
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("menuEdit")).perform(click())
    }

    @Test
    fun whenLoadedThenBindSelected() {
        assertBound(preset)
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset)
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset.copy(name = editedName))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenDisablePositiveButton() {
        fillUpUsing(preset.copy(name = emptyPreset.name))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenEnablePositiveButton() {
        fillUpUsing(preset.copy(name = editedName))
        onView(withText(R.string.button_save)).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenUpdateList() {
        with(preset.copy(name = editedName)) {
            fillUpUsing(this)
            onView(withText(R.string.button_save)).perform(click())
            onView(isTheRowFor(this)).check(matches(isDisplayed()))
        }
    }
}