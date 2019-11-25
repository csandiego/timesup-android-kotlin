package com.github.csandiego.timesup.editor

import android.os.Bundle
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.room.PresetDao
import com.github.csandiego.timesup.test.assertBound
import com.github.csandiego.timesup.test.fillUpUsing
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class EditPresetFragmentUITest2 {

    private lateinit var dao: PresetDao
    private val preset = Preset(id = 1L, name = "1 second", seconds = 1)
    private val editedName = "Edited Name"

    @Before
    fun setUp() = runBlocking<Unit> {
        val dagger = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().dagger
        dao = dagger.dao().apply {
            insert(preset)
        }
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PresetEditorViewModel(dagger.repository()) as T
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
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenSaveInDatabase() = runBlocking {
        val editedPreset = preset.copy(name = editedName)
        fillUpUsing(editedPreset)
        onView(withText(R.string.button_save)).perform(click())
        assertThat(dao.get(editedPreset.id)).isEqualTo(editedPreset)
    }
}