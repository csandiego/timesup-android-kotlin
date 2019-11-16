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
    protected lateinit var presets: List<Preset>

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private inline fun insertPresetsAndGetIds(
        presets: List<Preset>,
        callback: (List<Preset>) -> List<Long>
    ) = mutableListOf<Preset>().run {
        callback(presets).forEachIndexed { index, id ->
            add(index, presets[index].copy(id = id))
        }
        sortedBy { it.name }
    }

    @Before
    fun setUp() = runBlocking {
        presets = insertPresetsAndGetIds(_presets) {
            ApplicationProvider.getApplicationContext<TestTimesUpApplication>()
                .database.presetDao().insert(it)
        }
    }
}