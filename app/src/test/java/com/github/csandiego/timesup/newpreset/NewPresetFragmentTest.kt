package com.github.csandiego.timesup.newpreset

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NewPresetFragmentTest {

    private val preset = Preset(1, "3.5 hours and 10 seconds", 3, 30, 10)

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var scenario: FragmentScenario<NewPresetFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(application, TimesUpDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = DefaultPresetRepository(database.presetDao(), TestCoroutineScope())
        scenario = launchFragmentInContainer {
            NewPresetFragment {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return NewPresetViewModel(application, repository) as T
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
    fun whenInfoEnteredThenBindIntoViewModel() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        onView(withId(R.id.editTextHours)).perform(replaceText(preset.hours.toString()))
        onView(withId(R.id.editTextMinutes)).perform(replaceText(preset.minutes.toString()))
        onView(withId(R.id.editTextSeconds)).perform(replaceText(preset.seconds.toString()))
        scenario.onFragment {
            with (it.viewModel) {
                assertThat(name.value).isEqualTo(preset.name)
                assertThat(hours.value).isEqualTo(preset.hours)
                assertThat(minutes.value).isEqualTo(preset.minutes)
                assertThat(seconds.value).isEqualTo(preset.seconds)
            }
        }
    }

    @Test
    fun whenLoadedThenHideSaveButton() {
        onView(withId(R.id.buttonSave)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun whenNameEnteredAndDurationEmptyThenSaveButtonStaysHidden() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        onView(withId(R.id.buttonSave)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun whenNameEmptyAndDurationEnteredThenSaveButtonStaysHidden() {
        onView(withId(R.id.editTextHours)).perform(replaceText(preset.hours.toString()))
        onView(withId(R.id.editTextMinutes)).perform(replaceText(preset.minutes.toString()))
        onView(withId(R.id.editTextSeconds)).perform(replaceText(preset.seconds.toString()))
        onView(withId(R.id.buttonSave)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun whenNameAndDurationEnteredThenShowSaveButton() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        onView(withId(R.id.editTextHours)).perform(replaceText(preset.hours.toString()))
        onView(withId(R.id.editTextMinutes)).perform(replaceText(preset.minutes.toString()))
        onView(withId(R.id.editTextSeconds)).perform(replaceText(preset.seconds.toString()))
        onView(withId(R.id.buttonSave)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun givenSaveButtonShownWhenCreateThenUpdateRepository() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        onView(withId(R.id.editTextHours)).perform(replaceText(preset.hours.toString()))
        onView(withId(R.id.editTextMinutes)).perform(replaceText(preset.minutes.toString()))
        onView(withId(R.id.editTextSeconds)).perform(replaceText(preset.seconds.toString()))
        onView(withId(R.id.buttonSave)).perform(click())
        runBlockingTest {
            assertThat(repository.get(preset.id)).isEqualTo(preset)
        }
    }
}