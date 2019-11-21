package com.github.csandiego.timesup.timer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.github.csandiego.timesup.test.RoomDatabaseRule
import com.github.csandiego.timesup.test.insertAndReturnWithId
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerFragmentDefaultPresetRepositoryIntegrationTest {

    private lateinit var scenario: FragmentScenario<TimerFragment>
    private lateinit var timer: ManualTimer
    private val _preset = Preset(name = "2 seconds", seconds = 2)
    private lateinit var preset: Preset

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() = runBlocking {
        val dao = roomDatabaseRule.database.presetDao().apply {
            preset = insertAndReturnWithId(_preset)
        }
        val repository = DefaultPresetRepository(dao)
        timer = ManualTimer()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TimerViewModel(repository, timer) as T
            }
        }
        val args = Bundle().apply {
            putLong("presetId", preset.id)
        }
        scenario = launchFragmentInContainer(args, R.style.Theme_TimesUp) {
            TimerFragment(viewModelFactory)
        }
    }

    @Test
    fun givenValidPresetIdWhenLoadedThenLoadTimer() {
        scenario.onFragment {
            assertThat(timer.preset.value).isEqualTo(preset)
        }
    }
}