package com.github.csandiego.timesup.launcher

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LauncherViewModelTest {
    private val preset = Preset(1, "3.5 hours and 10 seconds", 3, 30, 10)

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: LauncherViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(application, TimesUpDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = database.presetDao().apply {
            runBlockingTest {
                insert(preset)
            }
        }
        repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = LauncherViewModel(application, repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun whenLoadValidPresetIdThenLiveDataContainsPreset() {
        viewModel.load(preset.id)
        assertThat(viewModel.preset.apply {
            observeForever {}
        }.value).isEqualTo(preset)
    }

    @Test
    fun whenLoadInvalidPresetIdThenLiveDataContainsPreset() {
        viewModel.load(0)
        assertThat(viewModel.preset.apply {
            observeForever {}
        }.value).isNull()
    }
}