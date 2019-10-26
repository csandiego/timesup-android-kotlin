package com.github.csandiego.timesup

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.presets.PresetsFragment
import com.github.csandiego.timesup.presets.PresetsViewModel
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetsUnitTest {

    private lateinit var viewModel: PresetsViewModel
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
        val dao = roomDatabaseRule.database.presetDao().apply {
            runBlockingTest {
                insert(TestData.presets)
            }
        }
        val repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = PresetsViewModel(repository)
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
    fun givenSelectedItemsWhenClearSelectionThenSelectionIsEmpty() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_close_button"))
            .perform(click())
        assertThat(viewModel.selection.value).isEmpty()
    }

    @Test
    fun givenSelectionWhenDeleteSelectedThenClearSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete"))
            .perform(click())
        assertThat(viewModel.selection.value).isEmpty()
    }

    @Test
    fun givenEmptySelectionWhenPresetLongClickedThenAddToSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        assertThat(viewModel.selection.value).containsExactly(presetsSortedByName[0].id)
    }

    @Test
    fun givenEmptySelectionWhenPresetClickedThenStartTimerForPreset() {
        scenario.onFragment {
            with(viewModel.startTimerForPreset) {
                removeObservers(it.viewLifecycleOwner)
                observe(it.viewLifecycleOwner) {}
            }
        }
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        assertThat(viewModel.startTimerForPreset.value).isEqualTo(presetsSortedByName[0])
    }

    @Test
    fun givenSelectionWhenUnselectedPresetClickedThenAddToSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        assertThat(viewModel.selection.value)
                .containsExactlyElementsIn(presetsSortedByName.subList(0, 2).map { it.id })
    }

    @Test
    fun givenSelectionWhenSelectedPresetClickedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        assertThat(viewModel.selection.value).isEmpty()
    }

    @Test
    fun givenSelectionWhenSelectedDeletedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        assertThat(viewModel.selection.value).isEmpty()
    }
}