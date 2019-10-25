package com.github.csandiego.timesup.presets

import android.content.Context
import android.provider.AlarmClock
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
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

    private lateinit var scenario: FragmentScenario<PresetsFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() {
        Intents.init()
        val dao = roomDatabaseRule.database.presetDao().apply {
            runBlockingTest {
                insert(presets)
            }
        }
        val repository = DefaultPresetRepository(dao, TestCoroutineScope())
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

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun whenLoadedThenRecyclerViewSortedByNameAscending() {
        scenario.onFragment {
            presetsSortedByName.forEachIndexed { index, preset ->
                assertThat(
                    it.view?.findViewById<RecyclerView>(R.id.recyclerView)
                        ?.findViewHolderForAdapterPosition(index)?.itemView?.tag
                ).isEqualTo(preset.hashCode())
            }
        }
    }

    @Test
    fun whenLoadedThenButtonNewDisplayed() {
        onView(withId(R.id.buttonNew)).check(matches(isDisplayed()))
    }

    @Test
    fun whenButtonNewClickedThenNavigateToNewPresetFragment() {
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
        onView(withId(R.id.buttonNew)).perform(click())
        verify(navController).navigate(
            PresetsFragmentDirections.actionPresetsFragmentToNewPresetFragment()
        )
    }

    @Test
    fun givenEmptySelectionWhenPresetClickedThenSendIntent() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        val s = presetsSortedByName[0].run {
            hours * 60 * 60 + minutes * 60 + seconds
        }
        intended(
            allOf(
                hasAction(AlarmClock.ACTION_SET_TIMER),
                hasExtra(AlarmClock.EXTRA_MESSAGE, presetsSortedByName[0].name),
                hasExtra(AlarmClock.EXTRA_LENGTH, s),
                hasExtra(AlarmClock.EXTRA_SKIP_UI, false)
            )
        )
    }

    @Test
    fun givenSelectionWhenEditMenuSelectedThenNavigateToPresetEditorFragment() {
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit"))
            .perform(click())
        verify(navController).navigate(
            PresetsFragmentDirections
                .actionPresetsFragmentToEditPresetFragment(presetsSortedByName[0].id)
        )
    }
}