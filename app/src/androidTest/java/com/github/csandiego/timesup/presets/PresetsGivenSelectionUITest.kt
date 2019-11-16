package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.R
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresetsGivenSelectionUITest : PresetsGivenDataUITest() {

    @Before
    override fun setUp() {
        super.setUp()
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                scrollToPosition<RecyclerView.ViewHolder>(1),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()),
                scrollToPosition<RecyclerView.ViewHolder>(0)
            )
    }

    @Test
    fun whenUnselectedClickedThenSelect() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(2),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click())
            )
        onView(withTagValue(equalTo(presets[2].hashCode()))).check(matches(isChecked()))
    }

    @Test
    fun whenSelectedClickedThenDeselect() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withTagValue(equalTo(presets[0].hashCode()))).check(matches(isNotChecked()))
    }

    @Test
    fun whenDeselectAllThenHideActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()),
                scrollToPosition<RecyclerView.ViewHolder>(1),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("action_mode_bar")).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenActionModeClosedThenDeselectAll() {
        onView(withResourceName("action_mode_close_button")).perform(click())
        repeat(2) {
            onView(withTagValue(equalTo(presets[it].hashCode()))).check(matches(isNotChecked()))
        }
    }

    @Test
    fun whenDeleteMenuClickedThenRemoveFromList() {
        onView(withResourceName("menuDelete")).perform(click())
        repeat(2) {
            onView(withTagValue(equalTo(presets[it].hashCode()))).check(doesNotExist())
        }
    }

    @Test
    fun whenSelectedSwipedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))
        onView(withResourceName("action_bar_title")).check(matches(withText("1")))
    }
}