package com.github.csandiego.timesup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove
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
    fun setUp() = runBlockingTest {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(presets)
    }

    @Test
    fun givenDataWhenLoadedThenDisplayByNameAscending() {
        repeat(presetsSortedByName.size - 1) {
            onView(withId(R.id.recyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(it))
            onView(
                allOf(
                    withParent(withId(R.id.recyclerView)),
                    hasDescendant(
                        allOf(
                            withId(R.id.textViewName),
                            withText(presetsSortedByName[it].name)
                        )
                    ),
                    hasDescendant(
                        allOf(
                            withId(R.id.textViewDuration),
                            withText(
                                String.format(
                                    "%02d:%02d:%02d",
                                    presetsSortedByName[it].hours,
                                    presetsSortedByName[it].minutes,
                                    presetsSortedByName[it].seconds
                                )
                            )
                        )
                    )
                )
            ).check(
                isCompletelyAbove(
                    allOf(
                        withParent(withId(R.id.recyclerView)),
                        hasDescendant(
                            allOf(
                                withId(R.id.textViewName),
                                withText(presetsSortedByName[it + 1].name)
                            )
                        ),
                        hasDescendant(
                            allOf(
                                withId(R.id.textViewDuration),
                                withText(
                                    String.format(
                                        "%02d:%02d:%02d",
                                        presetsSortedByName[it + 1].hours,
                                        presetsSortedByName[it + 1].minutes,
                                        presetsSortedByName[it + 1].seconds
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    @Test
    fun givenDataWhenPresetSwipeLeftThenRemoveFromList() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(doesNotExist())
    }

    @Test
    fun givenDataWhenPresetSwipeRightThenRemoveFromList() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(doesNotExist())
    }

    @Test
    fun givenEmptySelectionWhenUnselectedLongClickedThenSelect() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withTagValue(equalTo(presetsSortedByName[0].hashCode())))
            .check(matches(isChecked()))
    }

    @Test
    fun givenSelectionWhenUnselectedClickedThenSelect() {
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
    fun givenSelectionWhenSelectedClickedThenDeselect() {
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
    fun givenEmptySelectionWhenUnselectedLongClickedThenDisplayActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_bar"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenSelectionWhenDeselectAllThenHideActionMode() {
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
    fun givenSelectionWhenActionModeClosedThenDeselectAll() {
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
    fun givenSelectionWhenActionModeDisplayedThenShowSelectionCount() {
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

    @Test
    fun givenSingleSelectionWhenActionModeDisplayedThenShowEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenMultipleSelectionWhenActionModeDisplayedThenHideEditMenu() {
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
    fun givenSelectionWhenActionModeDisplayedThenShowDeleteMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuDelete"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenSelectionWhenDeleteMenuClickedThenRemoveFromList() {
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
}