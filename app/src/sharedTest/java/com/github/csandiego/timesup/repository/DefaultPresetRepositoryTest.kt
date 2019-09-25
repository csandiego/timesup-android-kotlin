package com.github.csandiego.timesup.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.github.csandiego.timesup.room.PresetDao
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
class DefaultPresetRepositoryTest {

    private val presets = listOf(
        Preset(1, "1 minute", 0, 1, 0),
        Preset(2, "2.5 minutes", 0, 2, 30),
        Preset(3, "5 minutes", 0, 5, 0),
        Preset(4, "1 hour", 1, 0, 0),
        Preset(5, "1.5 hours", 1, 30, 0)
    )

    private lateinit var dao: PresetDao
    private lateinit var repository: DefaultPresetRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() {
        dao = roomDatabaseRule.database.presetDao().apply {
            runBlockingTest {
                insertAll(presets)
            }
        }
        repository = DefaultPresetRepository(
            dao,
            TestCoroutineScope()
        )
    }

    @Test
    fun givenValidPresetIdWhenGetThenReturnPreset() {
        runBlockingTest {
            assertThat(repository.get(presets[0].id)).isNotNull()
        }
    }

    @Test
    fun givenInvalidPresetIdWhenGetThenReturnNull() {
        runBlockingTest {
            assertThat(repository.get(0)).isNull()
        }
    }

    fun givenNewPresetWhenCreatedThenUpdateDao() {
        val preset = Preset(6, "5 hours", 5, 0, 0)
        repository.create(preset)
        runBlockingTest {
            assertThat(dao.get(preset.id)).isEqualTo(preset)
        }
    }

    @Test
    fun givenValidPresetIdWhenGetAsLiveDataThenLiveDataContainsPreset() {
        assertThat(repository.getAsLiveData(presets[0].id).apply {
            observeForever {}
        }.value).isEqualTo(presets[0])
    }

    @Test
    fun givenInvalidPresetIdWhenGetAsLiveDataThenLiveDataContainsNull() {
        assertThat(repository.getAsLiveData(0).apply {
            observeForever {}
        }.value).isNull()
    }

    @Test
    fun whenGetAllByNameAscendingAsLiveDataThenLiveDataSortedByNameAscending() {
        assertThat(repository.getAllByNameAscendingAsLiveData().apply {
            observeForever {}
        }.value).containsExactlyElementsIn(presets.sortedBy { it.name })
    }

    @Test
    fun givenExistingPresetWhenDeleteThenUpdateDao() {
        repository.delete(presets[0])
        runBlockingTest {
            assertThat(dao.get(presets[0].id)).isNull()
        }
    }

    @Test
    fun givenExistingPresetsWhenDeleteAllThenUpdateDao() {
        repository.deleteAll(presets.subList(0, 2))
        runBlockingTest {
            repeat(2) {
                assertThat(dao.get(presets[it].id)).isNull()
            }
        }
    }

    @Test
    fun givenNewPresetWhenSaveThenUpdateDao() {
        val preset = Preset(presets.size.toLong(), "5 hours", 5, 0, 0)
        repository.save(preset)
        runBlockingTest {
            assertThat(dao.get(preset.id)).isEqualTo(preset)
        }
    }

    @Test
    fun givenExistingPresetWhenSaveThenUpdateDao() {
        val preset = presets[0].copy(name = "Test")
        repository.save(preset)
        runBlockingTest {
            assertThat(dao.get(preset.id)).isEqualTo(preset)
        }
    }
}