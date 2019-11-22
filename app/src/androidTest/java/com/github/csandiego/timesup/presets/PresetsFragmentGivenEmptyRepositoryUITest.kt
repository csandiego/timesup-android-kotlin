package com.github.csandiego.timesup.presets

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import org.junit.Test

class PresetsFragmentGivenEmptyRepositoryUITest : PresetsFragmentUITest() {

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
    fun whenPresetAddedThenHideEmptyView() {
        repository.saveBlocking(preset)
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenShowRecyclerView() {
        repository.saveBlocking(preset)
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}