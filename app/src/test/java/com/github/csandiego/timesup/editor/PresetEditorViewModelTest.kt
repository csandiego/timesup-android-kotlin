package com.github.csandiego.timesup.editor

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
class PresetEditorViewModelTest {

    private val preset = Preset(1, "3.5 hours and 10 seconds", 3, 30, 10)

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: PresetEditorViewModel

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
        viewModel = PresetEditorViewModel(application, repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun whenLoadedThenContainsEmptyPreset() {
        val preset = Preset()
        assertThat(viewModel.name.apply { observeForever {} }.value).isEqualTo(preset.name)
        assertThat(viewModel.hours.apply { observeForever {} }.value).isEqualTo(preset.hours)
        assertThat(viewModel.minutes.apply { observeForever {} }.value).isEqualTo(preset.minutes)
        assertThat(viewModel.seconds.apply { observeForever {} }.value).isEqualTo(preset.seconds)
    }

    @Test
    fun whenLoadPresetThenContainsLoadedPreset() {
        with(viewModel) {
            load(preset.id)
            assertThat(name.apply { observeForever {} }.value).isEqualTo(preset.name)
            assertThat(hours.apply { observeForever {} }.value).isEqualTo(preset.hours)
            assertThat(minutes.apply { observeForever {} }.value).isEqualTo(preset.minutes)
            assertThat(seconds.apply { observeForever {} }.value).isEqualTo(preset.seconds)
        }
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenHideSaveButton() {
        assertThat(viewModel.showSaveButton.apply { observeForever {} }.value).isFalse()
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenHideSaveButton() {
        with(viewModel) {
            name.apply { observeForever {} }.value = preset.name
            assertThat(showSaveButton.apply { observeForever {} }.value).isFalse()
        }
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenHideSaveButton() {
        with(viewModel) {
            hours.apply { observeForever {} }.value = preset.hours
            minutes.apply { observeForever {} }.value = preset.minutes
            seconds.apply { observeForever {} }.value = preset.seconds
            assertThat(showSaveButton.apply { observeForever {} }.value).isFalse()
        }
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenShowSaveButton() {
        with(viewModel) {
            name.apply { observeForever {} }.value = preset.name
            hours.apply { observeForever {} }.value = preset.hours
            minutes.apply { observeForever {} }.value = preset.minutes
            seconds.apply { observeForever {} }.value = preset.seconds
            assertThat(showSaveButton.apply { observeForever {} }.value).isTrue()
        }
    }

    @Test
    fun givenSaveButtonShownWhenSaveThenUpdateRepository() {
        val preset = Preset(2, "5 hours", 5, 0, 0)
        with(viewModel) {
            name.apply { observeForever {} }.value = preset.name
            hours.apply { observeForever {} }.value = preset.hours
            minutes.apply { observeForever {} }.value = preset.minutes
            seconds.apply { observeForever {} }.value = preset.seconds
            showSaveButton.apply { observeForever {} }
            save()
        }
        runBlockingTest {
            assertThat(repository.get(preset.id)).isEqualTo(preset)
        }
    }
}