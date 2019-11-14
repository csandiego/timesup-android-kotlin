package com.github.csandiego.timesup

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData.presets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetsNoDataUITest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun givenNoDataWhenLoadedThenShowEmptyView() {
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun givenNoDataWhenLoadedThenHideRecyclerView() {
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun givenNoDataWhenPresetAddedThenHideEmptyView() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(presets[0])
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun givenNoDataWhenPresetAddedThenShowRecyclerView() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
            .database.presetDao().insert(presets[0])
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}