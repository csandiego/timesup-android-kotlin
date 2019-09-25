package com.github.csandiego.timesup.presets

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository
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
class PresetsViewModelTest {

    private val presets = listOf(
        Preset(1, "1 minute", 0, 1, 0),
        Preset(2, "2.5 minutes", 0, 2, 30),
        Preset(3, "5 minutes", 0, 5, 0),
        Preset(4, "1 hour", 1, 0, 0),
        Preset(5, "1.5 hours", 1, 30, 0)
    )
    private val sortedPresets = presets.sortedBy { it.name }

    private lateinit var repository: PresetRepository
    private lateinit var viewModel: PresetsViewModel

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
                insertAll(presets)
            }
        }
        repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = PresetsViewModel(repository)
    }

    @Test
    fun whenLoadedThenPresetsSortedByNameAscending() {
        assertThat(viewModel.presets.apply { observeForever {} }.value)
            .containsExactlyElementsIn(sortedPresets)
    }

    @Test
    fun givenPresetWhenDeleteThenUpdateRepository() {
        with(viewModel) {
            presets.observeForever {}
            delete(sortedPresets[0])
        }
        runBlockingTest {
            assertThat(repository.get(sortedPresets[0].id)).isNull()
        }
    }

    @Test
    fun `givenUnselectedPresetWhenToggleSelectThenSelectionContainsPreset`() {
        with(viewModel) {
            toggleSelect(sortedPresets[0])
            assertThat(selection.apply { observeForever {} }.value).contains(sortedPresets[0])
        }
    }

    @Test
    fun `givenSelectedPresetWhenToggleSelectThenSelectionDoesNotContainPreset`() {
        with(viewModel) {
            toggleSelect(sortedPresets[0])
            toggleSelect(sortedPresets[0])
            assertThat(selection.apply { observeForever {} }.value).isEmpty()
        }
    }

    @Test
    fun `givenSelectedItemsWhenClearSelectionThenSelectionIsEmpty`() {
        with(viewModel) {
            toggleSelect(sortedPresets[0])
            clearSelection()
            assertThat(selection.apply { observeForever {} }.value).isEmpty()
        }
    }

    @Test
    fun `givenSelectionWhenDeleteSelectedThenUpdateRepository`() {
        with(viewModel) {
            repeat(2) {
                toggleSelect(sortedPresets[it])
            }
            deleteSelected()
        }
        runBlockingTest {
            repeat(2) {
                assertThat(repository.get(sortedPresets[it].id)).isNull()
            }
        }
    }

    @Test
    fun `givenSelectionWhenDeleteSelectedThenClearSelection`() {
        with(viewModel) {
            repeat(2) {
                toggleSelect(sortedPresets[it])
            }
            deleteSelected()
            assertThat(selection.value).isEmpty()
        }
    }
}