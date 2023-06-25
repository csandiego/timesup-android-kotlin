package com.github.csandiego.timesup.editor

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
import com.github.csandiego.timesup.test.fillUpUsing
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class NewPresetFragmentUITest2 {

    private lateinit var dao: PresetDao
    private val preset = Preset(name = "1 second", seconds = 1)

    @Before
    fun setUp() {
        val dagger = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().dagger
        dao = dagger.dao()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PresetEditorViewModel(dagger.repository()) as T
            }
        }
        launchFragment(themeResId = R.style.Theme_TimesUp) {
            NewPresetFragment(viewModelFactory)
        }
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedSaveInDatabase() = runBlocking {
        fillUpUsing(preset)
        onView(withText(R.string.button_save)).perform(click())
        assertThat(dao.get(1L)).isEqualTo(preset.copy(id = 1L))
    }
}