package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.timer.DurationFormatter
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresetsGivenEmptySelectionUITest : PresetsGivenDataUITest() {

    private fun withChildViewFor(preset: Preset) = allOf(
        withParent(withId(R.id.recyclerView)),
        hasDescendant(allOf(withId(R.id.textViewName), withText(preset.name))),
        hasDescendant(
            allOf(
                withId(R.id.textViewDuration),
                withText(DurationFormatter.format(preset.duration))
            )
        )
    )

    @Test
    fun whenLoadedThenDisplayByNameAscending() {
        repeat(presets.size - 1) {
            onView(withId(R.id.recyclerView)).perform(scrollToPosition<RecyclerView.ViewHolder>(it))
            onView(withChildViewFor(presets[it]))
                .check(isCompletelyAbove(withChildViewFor(presets[it + 1])))
        }
    }

    @Test
    fun whenSwipeLeftThenRemoveFromList() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
        onView(withTagValue(equalTo(presets[0].hashCode()))).check(doesNotExist())
    }

    @Test
    fun whenSwipeRightThenRemoveFromList() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        onView(withTagValue(equalTo(presets[0].hashCode()))).check(doesNotExist())
    }

    @Test
    fun whenLongClickedThenSelect() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withTagValue(equalTo(presets[0].hashCode()))).check(matches(isChecked()))
    }

    @Test
    fun whenSelectionMadeThenDisplayActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_bar")).check(matches(isDisplayed()))
    }

    @Test
    fun whenSingleSelectionMadeThenShowEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit")).check(matches(isDisplayed()))
    }

    @Test
    fun whenMultipleSelectionsMadeThenHideEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                scrollToPosition<RecyclerView.ViewHolder>(1),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuEdit")).check(doesNotExist())
    }

    @Test
    fun whenSelectionMadeThenShowDeleteMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuDelete")).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(1),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete")).check(matches(isDisplayed()))
    }

    @Test
    fun whenSelectionMadeThenShowSelectionCount() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_bar_title")).check(matches(withText("1")))
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(1),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("action_bar_title")).check(matches(withText("2")))
    }
}