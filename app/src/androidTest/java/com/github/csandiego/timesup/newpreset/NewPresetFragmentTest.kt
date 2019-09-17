package com.github.csandiego.timesup.newpreset

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.android.material.button.MaterialButton
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NewPresetFragmentTest {

    private val preset = Preset(1, "1 minute", 0, 1, 0)

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: NewPresetViewModel
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
        viewModel = NewPresetViewModel(application, repository)
        scenario = launchFragment(themeResId = R.style.Theme_TimesUp) {
            NewPresetFragment {
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
    fun whenNameEmptyAndDurationEmptyThenPositiveButtonDisabled() {
        onView(
            allOf(
                isAssignableFrom(MaterialButton::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenPositiveButtonDisabled() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        onView(
            allOf(
                isAssignableFrom(MaterialButton::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenPositiveButtonDisabled() {
        scenario.onFragment {
            with(viewModel) {
                hours = preset.hours
                minutes = preset.minutes
                seconds = preset.seconds
            }
        }
        onView(
            allOf(
                isAssignableFrom(MaterialButton::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenPositiveButtonEnabled() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        scenario.onFragment {
            with(viewModel) {
                hours = preset.hours
                minutes = preset.minutes
                seconds = preset.seconds
            }
        }
        onView(
            allOf(
                isAssignableFrom(MaterialButton::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(isEnabled()))
    }

    @Test
    fun givenPositiveButtonEnabledWhenCreateThenUpdateRepository() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name))
        scenario.onFragment {
            with(viewModel) {
                hours = preset.hours
                minutes = preset.minutes
                seconds = preset.seconds
            }
        }
        onView(
            allOf(
                isAssignableFrom(MaterialButton::class.java),
                withText(R.string.button_create)
            )
        ).perform(click())
        runBlockingTest {
            assertThat(repository.get(preset.id)).isEqualTo(preset)
        }
    }
}

