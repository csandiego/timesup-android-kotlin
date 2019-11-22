package com.github.csandiego.timesup.presets

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.repository.TestPresetRepository
import org.junit.Before

abstract class PresetsFragmentUITest {

    protected lateinit var repository: TestPresetRepository

    @Before
    fun setUp() {
        repository = TestPresetRepository()
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