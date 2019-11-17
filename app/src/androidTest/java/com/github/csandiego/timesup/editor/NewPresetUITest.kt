package com.github.csandiego.timesup.editor

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.timer.DurationFormatter
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewPresetUITest {
    
    private val emptyPreset = Preset()
    private val newPreset = Preset(name = "1 second", seconds = 1)

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        onView(withId(R.id.buttonNew)).perform(click())
    }

    @Test
    fun whenLoadedThenDisplayEmptyPreset() {
        onView(withId(R.id.editTextName)).check(matches(withText(emptyPreset.name)))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", emptyPreset.hours))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", emptyPreset.minutes))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", emptyPreset.seconds))))
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
        onView(withId(R.id.editTextName)).perform(replaceText(newPreset.name))
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
        ).perform(replaceText(newPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenDisablePositiveButton() {
        onView(withId(R.id.editTextName)).perform(replaceText(newPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenAddToList() {
        onView(withId(R.id.editTextName)).perform(replaceText(newPreset.name))
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.hours.toString()))
        onView(withId(R.id.numberPickerHours)).perform(click())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.minutes.toString()))
        onView(withId(R.id.numberPickerMinutes)).perform(click())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(newPreset.seconds.toString()))
        onView(withId(R.id.numberPickerSeconds)).perform(click())
        onView(withText(R.string.button_save)).perform(click())
        onView(withId(R.id.recyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(allOf(
            withParent(withId(R.id.recyclerView)),
            hasDescendant(
                allOf(
                    withId(R.id.textViewName),
                    withText(newPreset.name)
                )
            ),
            hasDescendant(
                allOf(
                    withId(R.id.textViewDuration),
                    withText(DurationFormatter.format(newPreset.duration))
                )
            )
        )).check(matches(isDisplayed()))
    }
}