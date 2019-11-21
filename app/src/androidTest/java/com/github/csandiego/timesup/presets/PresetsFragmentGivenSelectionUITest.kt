package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.test.isTheRowFor
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

class PresetsFragmentGivenSelectionUITest : PresetsFragmentGivenDataUITest() {

    @Before
    fun select() {
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
    }

    @Test
    fun whenUnselectedClickedThenSelect() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))
        onView(isTheRowFor(presets[2])).check(matches(isChecked()))
    }

    @Test
    fun whenSelectedClickedThenDeselect() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(isTheRowFor(presets[0])).check(matches(isNotChecked()))
    }

    @Test
    fun whenDeselectAllThenHideActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("action_mode_bar")).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenActionModeClosedThenDeselectAll() {
        onView(withResourceName("action_mode_close_button")).perform(click())
        presets.subList(0, 2).forEach {
            onView(isTheRowFor(it)).check(matches(isNotChecked()))
        }
    }

    @Test
    fun whenDeleteMenuClickedThenRemoveFromList() {
        onView(withResourceName("menuDelete")).perform(click())
        presets.subList(0, 2).forEach {
            onView(isTheRowFor(it)).check(doesNotExist())
        }
    }

    @Test
    fun whenDeleteMenuClickedThenDeleteFromRepository() {
        onView(withResourceName("menuDelete")).perform(click())
        presets.subList(0, 2).forEach {
            assertThat(repository.getBlocking(it.id)).isNull()
        }
    }

    @Test
    fun whenSelectedSwipedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))
        onView(withResourceName("action_bar_title")).check(matches(withText("1")))
    }
}