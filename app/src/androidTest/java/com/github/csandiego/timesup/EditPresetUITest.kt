package com.github.csandiego.timesup

import android.widget.Button
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
import com.github.csandiego.timesup.data.TestData.editPreset
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.data.TestData.updatedPreset
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditPresetUITest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(presets)
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
            .check(matches(withText(editPreset.name)))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", editPreset.hours))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", editPreset.minutes))))
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).check(matches(withText(String.format("%02d", editPreset.seconds))))
    }


    @Test
    fun whenNameEmptyAndDurationEmptyThenDisablePositiveButton() {
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
    fun whenNameNotEmptyAndDurationEmptyThenDisablePositiveButton() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(updatedPreset.name),
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
    fun whenNameEmptyAndDurationNotEmptyThenDisablePositiveButton() {
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
        ).perform(replaceText(updatedPreset.hours.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerMinutes))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(updatedPreset.minutes.toString()),
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
            replaceText(updatedPreset.seconds.toString()),
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
    fun whenNameNotEmptyAndDurationNotEmptyThenEnablePositiveButton() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(updatedPreset.name),
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
            replaceText(updatedPreset.hours.toString()),
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
            replaceText(updatedPreset.minutes.toString()),
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
            replaceText(updatedPreset.seconds.toString()),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenUpdateList() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(updatedPreset.name),
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
            replaceText(updatedPreset.hours.toString()),
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
            replaceText(updatedPreset.minutes.toString()),
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
            replaceText(updatedPreset.seconds.toString()),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).perform(click())
        onView(withId(R.id.recyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(
            allOf(
                isDescendantOfA(withTagValue(equalTo(updatedPreset.hashCode()))),
                withId(R.id.textViewName)
            )
        ).check(matches(withText(updatedPreset.name)))
        onView(
            allOf(
                isDescendantOfA(withTagValue(equalTo(updatedPreset.hashCode()))),
                withId(R.id.textViewDuration)
            )
        ).check(
            matches(
                withText(
                    String.format(
                        "%02d:%02d:%02d",
                        updatedPreset.hours,
                        updatedPreset.minutes,
                        updatedPreset.seconds
                    )
                )
            )
        )
    }
}