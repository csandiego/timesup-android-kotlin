package com.github.csandiego.timesup

import android.content.Context
import android.widget.Button
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData.newPreset
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.editor.NewPresetFragment
import com.github.csandiego.timesup.editor.PresetEditorViewModel
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewPresetIntegrationTest {

    private lateinit var repository: DefaultPresetRepository
    private lateinit var scenario: FragmentScenario<NewPresetFragment>

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() = runBlocking {
        val dao = roomDatabaseRule.database.presetDao().apply {
            insert(presets)
        }
        repository = DefaultPresetRepository(dao)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PresetEditorViewModel(repository) as T
            }
        }
        scenario = launchFragment(themeResId = R.style.Theme_TimesUp) {
            NewPresetFragment(viewModelFactory)
        }
    }

    @Test
    fun givenNameNotEmptyAndDurationNotEmptyWhenPositiveButtonClickedThenInsertIntoRepository() = runBlocking {
        onView(withId(R.id.editTextName))
            .perform(
                typeText(newPreset.name),
                closeSoftKeyboard()
            )
        onView(withId(R.id.numberPickerHours))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerHours)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(newPreset.hours.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerMinutes))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerMinutes)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(newPreset.minutes.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.numberPickerSeconds))
            .perform(longClick())
        onView(
            allOf(
                withParent(withId(R.id.numberPickerSeconds)),
                withClassName(endsWith("CustomEditText"))
            )
        ).perform(
            replaceText(newPreset.seconds.toString()),
            closeSoftKeyboard()
        )
        onView(
            allOf(
                isAssignableFrom(Button::class.java),
                withText(R.string.button_save)
            )
        ).perform(click())
        assertThat(repository.get(newPreset.id)).isEqualTo(newPreset)
    }
}