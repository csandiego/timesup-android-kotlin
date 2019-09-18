package com.github.csandiego.timesup.presets

import android.app.Application
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
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
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
    private val sortedPresets = presets.sortedBy { it.name }

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var scenario: FragmentScenario<PresetsFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Intents.init()
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
        val viewModel = PresetsViewModel(application, repository)
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment {
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
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
        Intents.release()
    }

    @Test
    fun whenLoadedThenRecyclerViewItemSortedByNameAscending() {
        sortedPresets.forEachIndexed { index, preset ->
            val tag = "list_item_preset_${preset.id}"
            onView(withId(R.id.recyclerView))
                .perform(scrollToPosition<PresetsViewHolder>(index))
            scenario.onFragment {
                assertThat(
                    it.view?.findViewById<RecyclerView>(R.id.recyclerView)
                        ?.findViewHolderForAdapterPosition(index)?.itemView?.tag
                ).isEqualTo(tag)
            }
            onView(
                allOf(
                    withParent(withTagValue(equalTo(tag))),
                    withId(R.id.textViewName)
                )
            ).check(matches(withText(preset.name)))
            onView(
                allOf(
                    withParent(withTagValue(equalTo(tag))),
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

    @Test
    fun whenPresetClickedThenSendIntent() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<PresetsViewHolder>(0),
                actionOnItemAtPosition<PresetsViewHolder>(0, click())
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
                scrollToPosition<PresetsViewHolder>(0),
                actionOnItemAtPosition<PresetsViewHolder>(0, swipeLeft())
            )
        runBlockingTest {
            assertThat(repository.get(sortedPresets[0].id)).isNull()
        }
    }

    @Test
    fun whenPresetSwipeRightThenDelete() {
        onView(withId(R.id.recyclerView))
            .perform(
                scrollToPosition<PresetsViewHolder>(0),
                actionOnItemAtPosition<PresetsViewHolder>(0, swipeRight())
            )
        runBlockingTest {
            assertThat(repository.get(sortedPresets[0].id)).isNull()
        }
    }
}