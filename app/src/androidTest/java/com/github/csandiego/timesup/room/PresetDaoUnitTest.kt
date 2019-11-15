package com.github.csandiego.timesup.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.junit.RoomDatabaseRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetDaoUnitTest {

    private lateinit var dao: PresetDao
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
    fun setUp() = runBlockingTest {
        dao = roomDatabaseRule.database.presetDao()
        val input = listOf(
            Preset(name = "2 seconds", seconds = 2),
            Preset(name = "3 seconds", seconds = 3),
            Preset(name = "1 second", seconds = 1)
        )
        val output = mutableListOf<Preset>()
        dao.insert(input).forEachIndexed { index, id ->
            output.add(index, input[index].copy(id = id))
        }
        presets = output
    }

    @Test
    fun whenGetAllByNameAscendingAsLiveDataThenLiveDataSortedByNameAscending() {
        val fetched = dao.getAllByNameAscendingAsLiveData().apply {
            observeForever {}
        }
        assertThat(fetched.value).containsExactlyElementsIn(presets.sortedBy { it.name })
    }

    @Test
    fun givenValidPresetIdWhenGetThenReturnPreset() = runBlockingTest {
        assertThat(dao.get(presets[0].id)).isEqualTo(presets[0])
    }

    @Test
    fun givenInvalidPresetIdWhenGetThenReturnNull() = runBlockingTest {
        assertThat(dao.get(invalidId)).isNull()
    }

    @Test
    fun givenValidPresetIdWhenDeleteByIdThenDeleteFromDatabase() = runBlockingTest {
        assertThat(dao.delete(presets[0].id)).isEqualTo(1)
    }

    @Test
    fun givenValidPresetIdsWhenDeleteAllByIdsThenDeleteFromDatabase() = runBlockingTest {
        val ids = presets.subList(0, 2).map { it.id }.toSet()
        assertThat(dao.delete(ids)).isEqualTo(ids.size)
    }
}