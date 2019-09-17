package com.github.csandiego.timesup.presets

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetsFragmentTest {

    private val presets = listOf(
        Preset(1, "1 minute", 0, 1, 0),
        Preset(2, "2.5 minutes", 0, 2, 30),
        Preset(3, "5 minutes", 0, 5, 0),
        Preset(4, "1 hour", 1, 0, 0),
        Preset(5, "1.5 hours", 1, 30, 0)
    )

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: PresetsViewModel
    private lateinit var scenario: FragmentScenario<PresetsFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(application, TimesUpDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = database.presetDao().apply {
            runBlockingTest {
                insertAll(presets)
            }
        }
        repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = PresetsViewModel(application, repository)
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return viewModel as T
                    }
                }
            }
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun whenLoadedThenRecyclerViewItemSortedByNameAscending() {
        presets.sortedBy { it.name }.forEachIndexed { index, preset ->
            onView(withId(R.id.recyclerView))
                .perform(scrollToPosition<PresetsViewHolder>(index))
            onView(withTagValue(equalTo("list_item_preset_${preset.id}")))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun whenLoadedThenFabNewDisplayed() {
        onView(withId(R.id.fabNew)).check(matches(isDisplayed()))
    }

    @Test
    fun whenFabNewClickedThenNavigateToNewPresetFragment() {
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
        onView(withId(R.id.fabNew)).perform(click())
        verify(navController).navigate(
            PresetsFragmentDirections.actionPresetsFragmentToNewPresetFragment()
        )
    }
}