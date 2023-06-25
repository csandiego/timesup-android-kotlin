package com.github.csandiego.timesup.presets

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PresetsFragmentGivenEmptyDatabaseUITest : PresetsFragmentUITest2() {

    private val preset = Preset(name = "1 second", seconds = 1)

    @Test
    fun whenViewDisplayedThenShowEmptyView() {
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun whenViewDisplayedThenHideRecyclerView() {
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenHideEmptyView() = runBlocking<Unit> {
        dao.save(preset)
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenShowRecyclerView() = runBlocking<Unit> {
        dao.save(preset)
        delay(500L)
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}