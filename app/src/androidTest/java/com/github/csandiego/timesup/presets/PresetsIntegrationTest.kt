package com.github.csandiego.timesup.presets

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresetsIntegrationTest {

    private lateinit var repository: PresetRepository
    private lateinit var scenario: FragmentScenario<PresetsFragment>
    private val _presets = listOf(
        Preset(name = "2 seconds", seconds = 2),
        Preset(name = "3 seconds", seconds = 3),
        Preset(name = "1 second", seconds = 1)
    )
    private lateinit var presets: List<Preset>

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

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
        val dao = roomDatabaseRule.database.presetDao()
        presets = insertPresetsAndGetIds(_presets) {
            dao.insert(it)
        }
        repository = DefaultPresetRepository(dao)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PresetsViewModel(repository) as T
            }
        }
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment(viewModelFactory)
        }
    }

    @Test
    fun whenPresetSwipeLeftThenDeleteFromRepository() = runBlocking {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
        assertThat(repository.get(presets[0].id)).isNull()
    }

    @Test
    fun whenPresetSwipeRightThenDeleteFromRepository() = runBlocking {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        assertThat(repository.get(presets[0].id)).isNull()
    }

    @Test
    fun givenSelectionWhenDeleteMenuClickedThenDeleteFromRepository() = runBlocking {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                scrollToPosition<RecyclerView.ViewHolder>(1),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete")).perform(click())
        repeat(2) {
            assertThat(repository.get(presets[it].id)).isNull()
        }
    }
}