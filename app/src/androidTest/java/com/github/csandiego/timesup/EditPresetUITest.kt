package com.github.csandiego.timesup

import android.widget.Button
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class EditPresetUITest {

    private val preset = presetsSortedByName[0]

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        activityScenarioRule.scenario.onActivity {
            runBlockingTest {
                ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
                    .database.presetDao().insert(presets)
            }
        }
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit")).perform(click())
    }

    @Test
    fun whenLoadedBindIntoView() {
        onView(withId(R.id.editTextName))
            .check(matches(withText(preset.name)))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", preset.hours))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", preset.minutes))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", preset.seconds))))
    }


    @Test
    fun whenNameEmptyAndDurationEmptyThenPositiveButtonDisabled() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(""),
                closeSoftKeyboard()
            )
        onView(withId(R.id.numberPickerHours))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerMinutes))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerSeconds))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenPositiveButtonDisabled() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(preset.name),
                closeSoftKeyboard()
            )
        onView(withId(R.id.numberPickerHours))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerMinutes))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerSeconds))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText("0"),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenPositiveButtonDisabled() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(""),
                closeSoftKeyboard()
            )
        onView(withId(R.id.numberPickerHours))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.hours.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerMinutes))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(preset.minutes.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerSeconds))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(preset.seconds.toString()),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenPositiveButtonEnabled() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(preset.name),
                closeSoftKeyboard()
            )
        onView(withId(R.id.numberPickerHours))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(preset.hours.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerMinutes))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(preset.minutes.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerSeconds))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(preset.seconds.toString()),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).check(matches(isEnabled()))
    }
}