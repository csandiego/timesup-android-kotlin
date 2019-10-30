package com.github.csandiego.timesup

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.presets.PresetsFragment
import com.github.csandiego.timesup.presets.PresetsViewModel
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetsIntegrationTest {

    private lateinit var repository: DefaultPresetRepository
    private lateinit var scenario: FragmentScenario<PresetsFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() =  runBlockingTest {
        val dao = roomDatabaseRule.database.presetDao().apply {
            insert(presets)
        }
        repository = DefaultPresetRepository(dao, this)
        val viewModel = PresetsViewModel(repository)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModel as T
            }
        }
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment(viewModelFactory)
        }
    }

    @Test
    fun givenDataWhenPresetSwipeLeftThenDeleteFromRepository() = runBlockingTest {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
        assertThat(repository.get(presetsSortedByName[0].id)).isNull()
    }

    @Test
    fun givenDataWhenPresetSwipeRightThenDeleteFromRepository() = runBlockingTest {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        assertThat(repository.get(presetsSortedByName[0].id)).isNull()
    }

    @Test
    fun givenSelectionWhenDeleteMenuClickedThenDeleteFromRepository() = runBlockingTest {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete"))
            .perform(click())
        repeat(2) {
            assertThat(repository.get(presetsSortedByName[it].id)).isNull()
        }
    }
}