package com.github.csandiego.timesup.presets

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.room.PresetDao
import org.junit.Before

abstract class PresetsFragmentUITest2 {

    protected lateinit var dao: PresetDao

    @Before
    fun setUp() {
        val dagger = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().dagger
        dao = dagger.dao()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PresetsViewModel(dagger.repository()) as T
            }
        }
        launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment(viewModelFactory)
        }
    }
}