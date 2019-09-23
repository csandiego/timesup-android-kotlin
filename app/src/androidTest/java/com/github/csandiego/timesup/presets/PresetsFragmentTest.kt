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
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.espresso.ViewMatchers.isActivated
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.*
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
    private val sortedPresets = presets.sortedBy { it.name }

    private lateinit var database: TimesUpDatabase
    private lateinit var scenario: FragmentScenario<PresetsFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Intents.init()
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TimesUpDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = database.presetDao().apply {
            runBlockingTest {
                insertAll(presets)
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
        database.close()
        Intents.release()
    }

    @Test
    fun whenLoadedThenRecyclerViewSortedByNameAscending() {
        scenario.onFragment {
            sortedPresets.forEachIndexed { index, preset ->
                assertThat(
                    it.view?.findViewById<RecyclerView>(R.id.recyclerView)
                        ?.findViewHolderForAdapterPosition(index)?.itemView?.tag
                ).isEqualTo(preset.hashCode())
            }
        }
    }

    @Test
    fun whenLoadedThenRecyclerViewDisplaysNameAndDuration() {
        sortedPresets.forEachIndexed { index, preset ->
            onView(withId(R.id.recyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(index))
            onView(
                allOf(
                    withParent(withTagValue(equalTo(preset.hashCode()))),
                    withId(R.id.textViewName)
                )
            ).check(matches(withText(preset.name)))
            onView(
                allOf(
                    withParent(withTagValue(equalTo(preset.hashCode()))),
                    withId(R.id.textViewDuration)
                )
            ).check(
                matches(
                    withText(
                        String.format(
                            "%02d:%02d:%02d",
                            preset.hours,
                            preset.minutes,
                            preset.seconds
                        )
                    )
                )
            )
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
        val s = sortedPresets[0].run {
            hours * 60 * 60 + minutes * 60 + seconds
        }
        intended(
            allOf(
                hasAction(AlarmClock.ACTION_SET_TIMER),
                hasExtra(AlarmClock.EXTRA_MESSAGE, sortedPresets[0].name),
                hasExtra(AlarmClock.EXTRA_LENGTH, s),
                hasExtra(AlarmClock.EXTRA_SKIP_UI, false)
            )
        )
    }

    @Test
    fun whenPresetSwipeLeftThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
        onView(withTagValue(equalTo(sortedPresets[0].hashCode())))
            .check(doesNotExist())
    }

    @Test
    fun whenPresetSwipeRightThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
        onView(withTagValue(equalTo(sortedPresets[0].hashCode())))
            .check(doesNotExist())
    }

    @Test
    fun givenEmptySelectionWhenPresetLongClickedThenAddToSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withTagValue(equalTo(sortedPresets[0].hashCode())))
            .check(matches(isActivated()))
    }

    @Test
    fun givenSelectionWhenUnselectedPresetClickedThenAddToSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        repeat(2) {
            onView(withTagValue(equalTo(sortedPresets[it].hashCode())))
                .check(matches(isActivated()))
        }
    }

    @Test
    fun givenSelectionWhenSelectedPresetClickedThenRemoveFromSelection() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        onView(withTagValue(equalTo(sortedPresets[0].hashCode())))
            .check(matches(not(isActivated())))
    }

    @Test
    fun givenEmptySelectionWhenPresetLongClickedThenDisplayActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_bar"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenActionModeDisplayedWhenDeselectAllThenHideActionMode() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
        onView(withResourceName("action_mode_bar"))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun givenActionModeDisplayedWhenActionModeClosedThenDeselectAll() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("action_mode_close_button"))
            .perform(click())
        onView(withTagValue(equalTo(sortedPresets[0].hashCode())))
            .check(matches(not(isActivated())))
    }

    @Test
    fun whenActionModeDisplayedThenShowMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit"))
            .check(matches(isDisplayed()))
        onView(withResourceName("menuDelete"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenActionModeDisplayedWhenOnePresetSelectedThenShowEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick())
            )
        onView(withResourceName("menuEdit"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenActionModeDisplayedWhenMoreThanOnePresetSelectedThenHideEditMenu() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuEdit"))
            .check(doesNotExist())
    }

    @Test
    fun givenSelectionWhenDeleteMenuSelectedThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<RecyclerView.ViewHolder>(0),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()),
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click())
            )
        onView(withResourceName("menuDelete"))
            .perform(click())
        repeat(2) {
            onView(withTagValue(equalTo(sortedPresets[it].hashCode())))
                .check(doesNotExist())
        }
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
                .actionPresetsFragmentToEditPresetFragment(sortedPresets[0].id)
        )
    }
}