package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.isTheRowFor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PresetsFragmentGivenEmptySelectionUITest : PresetsFragmentGivenLoadedRepositoryUITest() {

    @Test
    fun whenViewDisplayedThenOrderByNameAscending() {
        repeat(presets.size - 1) {
            onView(isTheRowFor(presets[it]))
                .check(isCompletelyAbove(isTheRowFor(presets[it + 1])))
        }
    }

    @Test
    fun whenPresetAddedThenAddToList() {
        val preset = Preset(name = "1 minute", minutes = 1)
        repository.saveBlocking(preset)
        onView(isTheRowFor(preset)).check(matches(isDisplayed()))
    }

    @Test
    fun whenPresetUpdatedThenUpdateList() {
        val preset = presets[0].copy(name = "Edited Name")
        repository.saveBlocking(preset)
        onView(isTheRowFor(presets[0])).check(doesNotExist())
        onView(isTheRowFor(preset)).check(matches(isDisplayed()))
    }

    @Test
    fun whenPresetDeletedThenRemoveFromList() {
        repository.deleteBlocking(presets[0].id)
        onView(isTheRowFor(presets[0])).check(doesNotExist())
    }

    @Test
    fun whenSwipeLeftThenDeleteFromRepository() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft()))
        assertThat(repository.getBlocking(presets[0].id)).isNull()
    }

    @Test
    fun whenSwipeRightThenDeleteFromRepository() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))
        assertThat(repository.getBlocking(presets[0].id)).isNull()
    }

    @Test
    fun whenLongClickedThenSelect() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(isTheRowFor(presets[0])).check(matches(isChecked()))
    }

    @Test
    fun whenSelectionMadeThenDisplayActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("action_mode_bar")).check(matches(isDisplayed()))
    }

    @Test
    fun whenSingleSelectionMadeThenShowEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("menuEdit")).check(matches(isDisplayed()))
    }

    @Test
    fun whenMultipleSelectionsMadeThenHideEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuEdit")).check(doesNotExist())
    }

    @Test
    fun whenSelectionMadeThenShowDeleteMenu() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("menuDelete")).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        onView(withResourceName("menuDelete")).check(matches(isDisplayed()))
    }

    @Test
    fun whenSelectionMadeThenShowSelectionCount() {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withResourceName("action_bar_title")).check(matches(withText("1")))
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        onView(withResourceName("action_bar_title")).check(matches(withText("2")))
    }
}