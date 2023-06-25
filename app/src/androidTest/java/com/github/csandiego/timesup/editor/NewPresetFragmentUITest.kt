package com.github.csandiego.timesup.editor

import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.TestPresetRepository
import com.github.csandiego.timesup.test.assertBound
import com.github.csandiego.timesup.test.fillUpUsing
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test

class NewPresetFragmentUITest {

    private lateinit var repository: TestPresetRepository
    private val emptyPreset = Preset()
    private val preset = Preset(name = "1 second", seconds = 1)

    @Before
    fun setUp() {
        repository = TestPresetRepository()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PresetEditorViewModel(repository) as T
            }
        }
        launchFragment(themeResId = R.style.Theme_TimesUp) {
            NewPresetFragment(viewModelFactory)
        }
    }

    @Test
    fun whenLoadedThenBindEmptyPreset() {
        assertBound(emptyPreset)
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset)
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset.copy(name = preset.name))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenDisablePositiveButton() {
        fillUpUsing(preset.copy(name = emptyPreset.name))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenEnablePositiveButton() {
        fillUpUsing(preset)
        onView(withText(R.string.button_save)).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedSaveInRepository() {
        fillUpUsing(preset)
        onView(withText(R.string.button_save)).perform(click())
        assertThat(repository.getBlocking(1L)).isEqualTo(preset.copy(id = 1L))
    }
}