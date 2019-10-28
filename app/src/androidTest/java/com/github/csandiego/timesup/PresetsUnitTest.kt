package com.github.csandiego.timesup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
import com.github.csandiego.timesup.presets.PresetsFragment
import com.github.csandiego.timesup.presets.PresetsFragmentDirections
import com.github.csandiego.timesup.presets.PresetsViewModel
import com.github.csandiego.timesup.repository.PresetRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class PresetsUnitTest {

    private lateinit var viewModel: PresetsViewModel
    private lateinit var scenario: FragmentScenario<PresetsFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val repository = mock(PresetRepository::class.java).apply {
            `when`(getAllByNameAscendingAsLiveData())
                .thenReturn(MutableLiveData(presetsSortedByName))
        }
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
    fun givenSingleSelectionWhenEditMenuClickedThenNavigateToPresetEditorFragment() {
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
            PresetsFragmentDirections.actionPresetsFragmentToEditPresetFragment(
                presetsSortedByName[0].id
            )
        )
    }

    @Test
    fun givenEmptySelectionWhenUnselectedClickedThenStartTimerForPreset() {
        scenario.onFragment {
            viewModel.startTimerForPreset.removeObservers(it.viewLifecycleOwner)
        }
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        assertThat(viewModel.startTimerForPreset.value).isEqualTo(presetsSortedByName[0])
    }

    @Test
    fun givenEmptySelectionWhenUnselectedLongClickedThenSelect() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        assertThat(viewModel.selection.value).containsExactly(presetsSortedByName[0].id)
    }

    @Test
    fun givenSelectionWhenUnselectedClickedThenSelect() {
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
    fun givenSelectionWhenSelectedClickedThenDeselect() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        assertThat(viewModel.selection.value).isEmpty()
    }

    @Test
    fun givenSelectionWhenSelectedSwipedThenDeselect() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        assertThat(viewModel.selection.value).containsExactly(presetsSortedByName[1].id)
    }

    @Test
    fun givenSelectedItemsWhenActionModeClosedThenSelectionIsEmpty() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("action_mode_close_button"))
            .perform(click())
        assertThat(viewModel.selection.value).isEmpty()
    }

    @Test
    fun givenSelectionWhenDeleteMenuClickedThenClearSelection() {
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
}