package com.github.csandiego.timesup.presets

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
class PresetsViewModelTest {

    private val presets = listOf(
        Preset(1, "1 minute", 0, 1, 0),
        Preset(2, "2.5 minutes", 0, 2, 30),
        Preset(3, "5 minutes", 0, 5, 0),
        Preset(4, "1 hour", 1, 0, 0),
        Preset(5, "1.5 hours", 1, 30, 0)
    )
    private val sortedPresets = presets.sortedBy { it.name }

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: PresetsViewModel

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
                insertAll(presets)
            }
        }
        repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = PresetsViewModel(application, repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun whenLoadedThenPresetsSortedByNameAscending() {
        assertThat(viewModel.presets.apply { observeForever {} }.value)
            .containsExactlyElementsIn(sortedPresets)
    }

    @Test
    fun givenPositionWhenDeleteThenUpdateRepository() {
        with (viewModel) {
            presets.observeForever {}
            delete(sortedPresets[0])
        }
        runBlockingTest {
            assertThat(repository.get(sortedPresets[0].id)).isNull()
        }
    }
}