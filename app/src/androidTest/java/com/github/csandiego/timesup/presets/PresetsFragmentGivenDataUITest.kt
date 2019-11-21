package com.github.csandiego.timesup.presets

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.github.csandiego.timesup.test.RoomDatabaseRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule

abstract class PresetsFragmentGivenDataUITest {

    private val _presets = listOf(
        Preset(name = "2 seconds", seconds = 2),
        Preset(name = "3 seconds", seconds = 3),
        Preset(name = "1 second", seconds = 1)
    )
    protected val presets = _presets.sortedBy { it.name }

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() = runBlocking<Unit> {
        val dao = roomDatabaseRule.database.presetDao().apply {
            insert(_presets)
        }
        val repository = DefaultPresetRepository(dao)
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