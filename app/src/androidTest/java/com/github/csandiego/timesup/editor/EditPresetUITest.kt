package com.github.csandiego.timesup.editor

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.timer.DurationFormatter
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditPresetUITest {

    private val emptyPreset = Preset()
    private val preset = Preset(name = "1 second", seconds = 1)
    private lateinit var insertedPreset: Preset
    private val editedPreset: Preset get() = insertedPreset.copy(name = "Edited name")

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        with(ApplicationProvider.getApplicationContext<TestTimesUpApplication>().database.presetDao()) {
            insertedPreset = preset.copy(id = insert(preset))
        }
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit")).perform(click())
    }

    @Test
    fun whenLoadedThenDisplaySelected() {
        onView(withId(R.id.editTextName)).check(matches(withText(insertedPreset.name)))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", insertedPreset.hours))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", insertedPreset.minutes))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", insertedPreset.seconds))))
    }


    @Test
    fun whenNameEmptyAndDurationEmptyThenDisablePositiveButton() {
        onView(withId(R.id.editTextName)).perform(replaceText(emptyPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(emptyPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(emptyPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(emptyPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenDisablePositiveButton() {
        onView(withId(R.id.editTextName)).perform(replaceText(editedPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(emptyPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(emptyPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(emptyPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenDisablePositiveButton() {
        onView(withId(R.id.editTextName)).perform(replaceText(emptyPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenEnablePositiveButton() {
        onView(withId(R.id.editTextName)).perform(replaceText(editedPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenUpdateList() {
        onView(withId(R.id.editTextName)).perform(replaceText(editedPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(editedPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).perform(click())
        onView(allOf(
            withParent(withId(R.id.recyclerView)),
            hasDescendant(
                allOf(
                    withId(R.id.textViewName),
                    withText(editedPreset.name)
                )
            ),
            hasDescendant(
                allOf(
                    withId(R.id.textViewDuration),
                    withText(DurationFormatter.format(editedPreset.duration))
                )
            )
        )).check(matches(isDisplayed()))
    }
}