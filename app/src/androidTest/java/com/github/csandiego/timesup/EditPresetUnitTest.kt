package com.github.csandiego.timesup

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.data.TestData.editPreset
import com.github.csandiego.timesup.data.TestData.updatedPreset
import com.github.csandiego.timesup.editor.EditPresetFragment
import com.github.csandiego.timesup.editor.PresetEditorViewModel
import com.github.csandiego.timesup.repository.PresetRepository
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class EditPresetUnitTest {

    private lateinit var viewModel: PresetEditorViewModel
    private lateinit var scenario: FragmentScenario<EditPresetFragment>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val repository = mock(PresetRepository::class.java).apply {
            `when`(getAsLiveData(editPreset.id)).thenReturn(MutableLiveData<Preset>(editPreset))
        }
        viewModel = PresetEditorViewModel(repository)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModel as T
            }
        }
        scenario = launchFragment(
            Bundle().apply {
                putLong("presetId", editPreset.id)
            },
            R.style.Theme_TimesUp
        ) {
            EditPresetFragment(viewModelFactory)
        }
    }

    @Test
    fun whenNameAndDurationEnteredThenBindIntoViewModel() {
        onView(withId(R.id.editTextName))
            .perform(
                replaceText(updatedPreset.name),
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
            replaceText(updatedPreset.hours.toString()),
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
            replaceText(updatedPreset.minutes.toString()),
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
            replaceText(updatedPreset.seconds.toString()),
            closeSoftKeyboard()
        )
        with(viewModel) {
            assertThat(name.value).isEqualTo(updatedPreset.name)
            assertThat(hours.value).isEqualTo(updatedPreset.hours)
            assertThat(minutes.value).isEqualTo(updatedPreset.minutes)
            assertThat(seconds.value).isEqualTo(updatedPreset.seconds)
        }
    }
}