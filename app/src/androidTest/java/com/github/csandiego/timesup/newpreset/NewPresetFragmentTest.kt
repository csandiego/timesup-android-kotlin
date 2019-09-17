package com.github.csandiego.timesup.newpreset

import android.app.Application
import android.widget.Button
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
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
import org.hamcrest.Matchers.*
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
    }

    @Test
    fun whenNameAndDurationEnteredThenBindIntoViewModel() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name), closeSoftKeyboard())
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.hours.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.minutes.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.seconds.toString()), closeSoftKeyboard())
        with(viewModel) {
            assertThat(name).isEqualTo(preset.name)
            assertThat(hours).isEqualTo(preset.hours)
            assertThat(minutes).isEqualTo(preset.minutes)
            assertThat(seconds).isEqualTo(preset.seconds)
        }
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenPositiveButtonDisabled() {
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenPositiveButtonDisabled() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name), closeSoftKeyboard())
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenPositiveButtonDisabled() {
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.hours.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.minutes.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.seconds.toString()), closeSoftKeyboard())
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenPositiveButtonEnabled() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name), closeSoftKeyboard())
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.hours.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.minutes.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.seconds.toString()), closeSoftKeyboard())
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_create)
            )
        ).check(matches(isEnabled()))
    }

    @Test
    fun givenPositiveButtonEnabledWhenCreateThenUpdateRepository() {
        onView(withId(R.id.editTextName)).perform(typeText(preset.name), closeSoftKeyboard())
        onView(withId(R.id.numberPickerHours)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.hours.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerMinutes)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.minutes.toString()), closeSoftKeyboard())
        onView(withId(R.id.numberPickerSeconds)).perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(replaceText(preset.seconds.toString()), closeSoftKeyboard())
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_create)
            )
        ).perform(click())
        runBlockingTest {
            assertThat(repository.get(preset.id)).isEqualTo(preset)
        }
    }
}