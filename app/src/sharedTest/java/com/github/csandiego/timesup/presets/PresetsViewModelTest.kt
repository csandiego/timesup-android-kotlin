package com.github.csandiego.timesup.presets

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData.presets
import com.github.csandiego.timesup.data.TestData.presetsSortedByName
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
class PresetsViewModelTest {

    private lateinit var repository: DefaultPresetRepository
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
                insert(presets)
            }
        }
        repository = DefaultPresetRepository(dao, TestCoroutineScope())
        viewModel = PresetsViewModel(repository)
    }

    @Test
    fun whenLoadedThenPresetsSortedByNameAscending() {
        assertThat(viewModel.presets.apply { observeForever {} }.value)
            .containsExactlyElementsIn(presetsSortedByName)
    }

    @Test
    fun givenPresetWhenDeleteThenUpdateRepository() {
        with(viewModel) {
            presets.observeForever {}
            delete(presetsSortedByName[0])
        }
        runBlockingTest {
            assertThat(repository.get(presetsSortedByName[0].id)).isNull()
        }
    }

    @Test
    fun givenSelectedItemsWhenClearSelectionThenSelectionIsEmpty() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            clearSelection()
            assertThat(selection.value).isEmpty()
        }
    }

    @Test
    fun givenSelectionWhenDeleteSelectedThenUpdateRepository() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            onClick(presetsSortedByName[1])
            deleteSelected()
        }
        runBlockingTest {
            repeat(2) {
                assertThat(repository.get(presetsSortedByName[it].id)).isNull()
            }
        }
    }

    @Test
    fun givenSelectionWhenDeleteSelectedThenClearSelection() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            onClick(presetsSortedByName[1])
            deleteSelected()
            assertThat(selection.value).isEmpty()
        }
    }

    @Test
    fun givenEmptySelectionWhenPresetLongClickedThenAddToSelection() {
        with(viewModel) {
            assertThat(onLongClick(presetsSortedByName[0])).isTrue()
            assertThat(selection.value).containsExactly(presetsSortedByName[0].id)
        }
    }

    @Test
    fun givenSelectionWhenPresetLongClickedThenReturnFalse() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            assertThat(onLongClick(presetsSortedByName[1])).isFalse()
        }
    }

    @Test
    fun givenEmptySelectionWhenPresetClickedThenStartTimerForPreset() {
        with(viewModel) {
            onClick(presetsSortedByName[0])
            assertThat(startTimerForPreset.value).isEqualTo(presetsSortedByName[0])
        }
    }

    @Test
    fun givenSelectionWhenUnselectedPresetClickedThenAddToSelection() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            onClick(presetsSortedByName[1])
            assertThat(selection.value)
                .containsExactlyElementsIn(presetsSortedByName.subList(0, 2).map { it.id })
        }
    }

    @Test
    fun givenSelectionWhenSelectedPresetClickedThenRemoveFromSelection() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            onClick(presetsSortedByName[0])
            assertThat(selection.value).isEmpty()
        }
    }

    @Test
    fun givenSelectionWhenSelectedDeletedThenRemoveFromSelection() {
        with(viewModel) {
            onLongClick(presetsSortedByName[0])
            delete(presetsSortedByName[0])
            assertThat(selection.value).isEmpty()
        }
    }
}