package com.github.csandiego.timesup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
class PresetsUITest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @UiThreadTest
    @Before
    fun setUp() {
        runBlockingTest {
            ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
                .database.presetDao().insert(presets)
        }
    }

    @Test
    fun whenLoadedThenRecyclerViewDisplaysNameAndDuration() {
        presetsSortedByName.forEachIndexed { index, preset ->
            onView(withId(R.id.recyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(index))
            onView(
                allOf(
                    isDescendantOfA(withTagValue(equalTo(preset.hashCode()))),
                    withId(R.id.textViewName)
                )
            ).check(matches(withText(preset.name)))
            onView(
                allOf(
                    isDescendantOfA(withTagValue(equalTo(preset.hashCode()))),
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

    @Test
    fun whenPresetSwipeLeftThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(doesNotExist())
    }

    @Test
    fun whenPresetSwipeRightThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(doesNotExist())
    }

    @Test
    fun givenEmptySelectionWhenPresetLongClickedThenAddToSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(matches(isChecked()))
    }

    @Test
    fun givenSelectionWhenUnselectedPresetClickedThenAddToSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        repeat(2) {
            onView(withTagValue(equalTo(presetsSortedByName[it].hashCode())))
                .check(matches(isChecked()))
        }
    }

    @Test
    fun givenSelectionWhenSelectedPresetClickedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(matches(isNotChecked()))
    }

    @Test
    fun givenEmptySelectionWhenPresetLongClickedThenDisplayActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_bar"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenActionModeDisplayedWhenDeselectAllThenHideActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        onView(withResourceName("action_mode_bar"))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun givenActionModeDisplayedWhenActionModeClosedThenDeselectAll() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_close_button"))
            .perform(click())
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(matches(isNotChecked()))
    }

    @Test
    fun whenActionModeDisplayedThenShowMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit"))
            .check(matches(isDisplayed()))
        onView(withResourceName("menuDelete"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenActionModeDisplayedThenTitleIsSelectionCount() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("action_bar_title"))
            .check(matches(withText("2")))
    }

    @Test
    fun givenActionModeDisplayedWhenOnePresetSelectedThenShowEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenActionModeDisplayedWhenMoreThanOnePresetSelectedThenHideEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuEdit"))
            .check(doesNotExist())
    }

    @Test
    fun givenSelectionWhenDeleteMenuSelectedThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete"))
            .perform(click())
        repeat(2) {
            onView(withTagValue(equalTo(presetsSortedByName[it].hashCode())))
                .check(doesNotExist())
        }
    }

    @Test
    fun givenSelectionWhenSelectedSwipedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, swipeRight())
            )
        onView(withResourceName("action_bar_title"))
            .check(matches(withText("1")))
    }
}