package com.github.csandiego.timesup.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.RoomDatabaseRule
import com.github.csandiego.timesup.test.insertAndReturnWithId
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PresetDaoUnitTest {

    private lateinit var dao: PresetDao
    private val _presets = listOf(
        Preset(name = "2 seconds", seconds = 2),
        Preset(name = "3 seconds", seconds = 3),
        Preset(name = "1 second", seconds = 1)
    )
    private lateinit var presets: List<Preset>
    private val invalidId = -1L

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() = runBlocking {
        dao = roomDatabaseRule.database.presetDao().apply {
            presets = insertAndReturnWithId(_presets)
        }
    }

    @Test
    fun whenGetAllByNameAscendingAsLiveDataThenLiveDataSortedByNameAscending() {
        val fetched = dao.getAllByNameAscendingAsLiveData().apply {
            observeForever {}
        }
        assertThat(fetched.value).containsExactlyElementsIn(presets.sortedBy { it.name }).inOrder()
    }

    @Test
    fun givenValidPresetIdWhenGetThenReturnPreset() = runBlocking<Unit> {
        with(presets.first()) {
            assertThat(dao.get(id)).isEqualTo(this)
        }
    }


    @Test
    fun givenInvalidPresetIdWhenGetThenReturnNull() = runBlocking {
        assertThat(dao.get(invalidId)).isNull()
    }

    @Test
    fun givenValidPresetIdWhenDeleteThenDeleteSingleItem() = runBlocking {
        assertThat(dao.delete(presets.first().id)).isEqualTo(1)
    }

    @Test
    fun givenValidPresetIdsWhenDeleteThenDeleteSameAmount() = runBlocking<Unit> {
        with(presets.subList(0, 2).map { it.id }.toSet()) {
            assertThat(dao.delete(this)).isEqualTo(size)
        }
    }
}