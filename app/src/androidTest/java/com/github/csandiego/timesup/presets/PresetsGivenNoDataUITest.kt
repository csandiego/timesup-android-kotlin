package com.github.csandiego.timesup.presets

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresetsGivenNoDataUITest {

    private val preset = Preset(name = "1 second", seconds = 1)

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun whenLoadedThenShowEmptyView() {
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun whenLoadedThenHideRecyclerView() {
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenHideEmptyView() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(preset)
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenShowRecyclerView() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(preset)
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}