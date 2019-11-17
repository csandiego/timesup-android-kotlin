package com.github.csandiego.timesup.presets

import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.timer.DurationFormatter
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
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


    protected fun withChildViewFor(preset: Preset): Matcher<View> = allOf(
        withParent(withId(R.id.recyclerView)),
        hasDescendant(
            allOf(
                withId(R.id.textViewName),
                withText(preset.name)
            )
        ),
        hasDescendant(
            allOf(
                withId(R.id.textViewDuration),
                withText(DurationFormatter.format(preset.duration))
            )
        )
    )
}