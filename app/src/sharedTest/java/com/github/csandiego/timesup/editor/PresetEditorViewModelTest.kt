package com.github.csandiego.timesup.editor

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetEditorViewModelTest {

    private val preset = presets[0]

    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: PresetEditorViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() {
        val dao = roomDatabaseRule.database.presetDao().apply {
            runBlockingTest {
                insert(preset)
            }
        }
        repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = PresetEditorViewModel(repository).apply { 
            name.observeForever {}
            hours.observeForever {}
            minutes.observeForever {}
            seconds.observeForever {}
            showSaveButton.observeForever {}
        }
    }

    @Test
    fun whenLoadedThenContainsEmptyPreset() {
        val preset = Preset()
        assertThat(viewModel.name.value).isEqualTo(preset.name)
        assertThat(viewModel.hours.value).isEqualTo(preset.hours)
        assertThat(viewModel.minutes.value).isEqualTo(preset.minutes)
        assertThat(viewModel.seconds.value).isEqualTo(preset.seconds)
    }

    @Test
    fun whenLoadPresetThenContainsLoadedPreset() {
        with(viewModel) {
            load(preset.id)
            assertThat(name.value).isEqualTo(preset.name)
            assertThat(hours.value).isEqualTo(preset.hours)
            assertThat(minutes.value).isEqualTo(preset.minutes)
            assertThat(seconds.value).isEqualTo(preset.seconds)
        }
    }

    @Test
    fun whenNameEmptyAndDurationEmptyThenHideSaveButton() {
        assertThat(viewModel.showSaveButton.value).isFalse()
    }

    @Test
    fun whenNameNotEmptyAndDurationEmptyThenHideSaveButton() {
        with(viewModel) {
            name.value = preset.name
            assertThat(showSaveButton.value).isFalse()
        }
    }

    @Test
    fun whenNameEmptyAndDurationNotEmptyThenHideSaveButton() {
        with(viewModel) {
            hours.value = preset.hours
            minutes.value = preset.minutes
            seconds.value = preset.seconds
            assertThat(showSaveButton.value).isFalse()
        }
    }

    @Test
    fun whenNameNotEmptyAndDurationNotEmptyThenShowSaveButton() {
        with(viewModel) {
            name.value = preset.name
            hours.value = preset.hours
            minutes.value = preset.minutes
            seconds.value = preset.seconds
            assertThat(showSaveButton.value).isTrue()
        }
    }

    @Test
    fun givenSaveButtonShownWhenSaveThenUpdateRepository() {
        val preset = Preset(2, "5 hours", 5, 0, 0)
        with(viewModel) {
            name.value = preset.name
            hours.value = preset.hours
            minutes.value = preset.minutes
            seconds.value = preset.seconds
            save()
        }
        runBlockingTest {
            assertThat(repository.get(preset.id)).isEqualTo(preset)
        }
    }
}