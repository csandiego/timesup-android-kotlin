package com.github.csandiego.timesup.presets

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.PresetDao
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.github.csandiego.timesup.test.RoomDatabaseRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PresetsFragmentGivenNoDataUITest {

    private lateinit var dao: PresetDao
    private lateinit var scenario: FragmentScenario<PresetsFragment>
    private val preset = Preset(name = "1 second", seconds = 1)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class,
        true
    )

    @Before
    fun setUp() {
        dao = roomDatabaseRule.database.presetDao()
        val repository = DefaultPresetRepository(dao)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PresetsViewModel(repository) as T
            }
        }
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment(viewModelFactory)
        }
    }

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
    fun whenPresetAddedThenHideEmptyView() {
        scenario.onFragment {
            runBlocking {
                dao.insert(preset)
            }
        }
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenShowRecyclerView() {
        scenario.onFragment {
            runBlocking {
                dao.insert(preset)
            }
        }
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}