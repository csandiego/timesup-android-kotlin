package com.github.csandiego.timesup.presets

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.TestPresetRepository
import org.junit.Before

abstract class PresetsFragmentGivenDataUITest {

    protected lateinit var repository: TestPresetRepository
    private val _presets = listOf(
        Preset(id = 1L, name = "2 seconds", seconds = 2),
        Preset(id = 2L, name = "3 seconds", seconds = 3),
        Preset(id = 3L, name = "1 second", seconds = 1)
    )
    protected val presets = _presets.sortedBy { it.name }

    @Before
    fun setUp() {
        repository = TestPresetRepository(_presets)
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PresetsViewModel(repository) as T
            }
        }
        launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment(viewModelFactory)
        }
    }
}