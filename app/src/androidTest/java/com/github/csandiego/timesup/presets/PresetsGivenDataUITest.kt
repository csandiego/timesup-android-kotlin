package com.github.csandiego.timesup.presets

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule

abstract class PresetsGivenDataUITest {

    private val _presets = listOf(
        Preset(name = "2 seconds", seconds = 2),
        Preset(name = "3 seconds", seconds = 3),
        Preset(name = "1 second", seconds = 1)
    )
    protected val presets = _presets.sortedBy { it.name }

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        ApplicationProvider.getApplicationContext<TestTimesUpApplication>().database.presetDao()
            .insert(_presets)
    }
}