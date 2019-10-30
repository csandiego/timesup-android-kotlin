package com.github.csandiego.timesup

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
import com.github.csandiego.timesup.data.TestData.newPreset
import com.github.csandiego.timesup.editor.NewPresetFragment
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
class NewPresetUnitTest {

    private lateinit var viewModel: PresetEditorViewModel
    private lateinit var scenario: FragmentScenario<NewPresetFragment>

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
        scenario = launchFragment(themeResId = R.style.Theme_TimesUp) {
            NewPresetFragment(viewModelFactory)
        }
    }

    @Test
    fun whenNameAndDurationEnteredThenBindIntoViewModel() {
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
        with(viewModel) {
            assertThat(name.value).isEqualTo(newPreset.name)
            assertThat(hours.value).isEqualTo(newPreset.hours)
            assertThat(minutes.value).isEqualTo(newPreset.minutes)
            assertThat(seconds.value).isEqualTo(newPreset.seconds)
        }
    }
}