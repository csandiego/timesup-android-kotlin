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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PresetsFragmentGivenLoadedDatabase : PresetsFragmentUITest2() {

    private val _presets = listOf(
        Preset(id = 1L, name = "2 seconds", seconds = 2),
        Preset(id = 2L, name = "3 seconds", seconds = 3),
        Preset(id = 3L, name = "1 second", seconds = 1)
    )
    private val presets = _presets.sortedBy { it.name }

    @Before
    fun load() = runBlocking<Unit> {
        dao.insert(_presets)
    }

    @Test
    fun whenViewDisplayedThenOrderByNameAscending() {
        repeat(presets.size - 1) {
            onView(isTheRowFor(presets[it]))
                .check(isCompletelyAbove(isTheRowFor(presets[it + 1])))
        }
    }

    @Test
    fun whenPresetAddedThenAddToList() = runBlocking<Unit> {
        val preset = Preset(name = "1 minute", minutes = 1)
        dao.save(preset)
        onView(isTheRowFor(preset)).check(matches(isDisplayed()))
    }

    @Test
    fun whenPresetUpdatedThenUpdateList() = runBlocking<Unit> {
        val preset = presets[0].copy(name = "Edited Name")
        dao.save(preset)
        delay(500L)
        onView(isTheRowFor(presets[0])).check(doesNotExist())
        onView(isTheRowFor(preset)).check(matches(isDisplayed()))
    }

    @Test
    fun whenPresetDeletedThenRemoveFromList() = runBlocking<Unit> {
        dao.delete(presets[0].id)
        delay(500L)
        onView(isTheRowFor(presets[0])).check(doesNotExist())
    }

    @Test
    fun whenSwipeLeftThenDeleteFromDatabase() = runBlocking {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft()))
        assertThat(dao.get(presets[0].id)).isNull()
    }

    @Test
    fun whenSwipeRightThenDeleteFromDatabase() = runBlocking {
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))
        assertThat(dao.get(presets[0].id)).isNull()
    }

    @Test
    fun whenDeleteMenuClickedThenDeleteFromDatabase() = runBlocking {
        onView(withId(R.id.recyclerView))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete")).perform(click())
        presets.subList(0, 2).forEach {
            assertThat(dao.get(it.id)).isNull()
        }
    }
}