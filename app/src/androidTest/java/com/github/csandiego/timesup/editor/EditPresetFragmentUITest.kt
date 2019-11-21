package com.github.csandiego.timesup.editor

import android.os.Bundle
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

class EditPresetFragmentUITest {

    private lateinit var repository: TestPresetRepository
    private val emptyPreset = Preset()
    private val preset = Preset(id = 1L, name = "1 second", seconds = 1)
    private val editedName = "Edited Name"

    @Before
    fun setUp() {
        repository = TestPresetRepository(preset)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PresetEditorViewModel(repository) as T
            }
        }
        val args = Bundle().apply {
            putLong("presetId", preset.id)
        }
        launchFragment(args, R.style.Theme_TimesUp) {
            EditPresetFragment(viewModelFactory)
        }
    }

    @Test
    fun whenLoadedThenBindSelected() {
        assertBound(preset)
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset)
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenDisablePositiveButton() {
        fillUpUsing(emptyPreset.copy(name = editedName))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenDisablePositiveButton() {
        fillUpUsing(preset.copy(name = emptyPreset.name))
        onView(withText(R.string.button_save)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenEnablePositiveButton() {
        fillUpUsing(preset.copy(name = editedName))
        onView(withText(R.string.button_save)).check(matches(isEnabled()))
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenSaveInRepository() {
        val editedPreset = preset.copy(name = editedName)
        fillUpUsing(editedPreset)
        onView(withText(R.string.button_save)).perform(click())
        assertThat(repository.getBlocking(editedPreset.id)).isEqualTo(editedPreset)
    }
}