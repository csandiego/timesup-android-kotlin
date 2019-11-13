package com.github.csandiego.timesup.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.data.TestData.presets
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

    private val scope = TestCoroutineScope()

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
    fun setUp() = scope.runBlockingTest {
        dao = roomDatabaseRule.database.presetDao().apply {
            insert(presets)
        }
        repository = DefaultPresetRepository(dao, this)
    }

    @Test
    fun givenValidPresetIdWhenGetThenReturnPreset() = runBlockingTest {
        assertThat(repository.get(presets[0].id)).isNotNull()
    }

    @Test
    fun givenInvalidPresetIdWhenGetThenReturnNull() = runBlockingTest {
        assertThat(repository.get(0)).isNull()
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
    fun givenNewPresetWhenSaveThenUpdateDao() = scope.runBlockingTest {
        val preset = Preset(presets.size.toLong(), "5 hours", 5, 0, 0)
        repository.save(preset)
        assertThat(dao.get(preset.id)).isEqualTo(preset)
    }

    @Test
    fun givenExistingPresetWhenSaveThenUpdateDao() = scope.runBlockingTest {
        val preset = presets[0].copy(name = "Test")
        repository.save(preset)
        assertThat(dao.get(preset.id)).isEqualTo(preset)
    }

    @Test
    fun givenValidPresetIdWhenDeleteThenUpdateDao() = scope.runBlockingTest {
        repository.delete(presets[0].id)
        assertThat(dao.get(presets[0].id)).isNull()
    }

    @Test
    fun giveValidPresetIdsWhenDeleteThenUpdateDao() = scope.runBlockingTest {
        val ids = presets.subList(0, 4).map { it.id }.toSet()
        repository.delete(ids)
        ids.forEach {
            assertThat(dao.get(it)).isNull()
        }
    }
}