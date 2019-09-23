package com.github.csandiego.timesup

import android.widget.Button
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddPresetTest {

    private val preset = Preset(1, "1 minute", 0, 1, 0)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenAddNewPresetThenUpdateList() {
        onView(withId(R.id.buttonNew))
            .perform(click())
        onView(withId(R.id.editTextName))
            .perform(
                typeText(preset.name),
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
        ).perform(click())
        onView(withId(R.id.recyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            onView(
                allOf(
                    withParent(withTagValue(equalTo(preset.hashCode()))),
                    withId(R.id.textViewName)
                )
            ).check(matches(withText(preset.name)))
            onView(
                allOf(
                    withParent(withTagValue(equalTo(preset.hashCode()))),
                    withId(R.id.textViewDuration)
                )
            ).check(
                matches(
                    withText(
                        String.format(
                            "%02d:%02d:%02d",
                            preset.hours,
                            preset.minutes,
                            preset.seconds
                        )
                    )
                )
            )
    }
}