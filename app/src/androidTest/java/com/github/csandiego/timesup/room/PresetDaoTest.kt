package com.github.csandiego.timesup.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.TestData.presets
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
class PresetDaoTest {

    private lateinit var dao: PresetDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val roomDatabaseRule = RoomDatabaseRule(
        ApplicationProvider.getApplicationContext<Context>(),
        TimesUpDatabase::class
    )

    @Before
    fun setUp() = runBlockingTest {
        dao = roomDatabaseRule.database.presetDao().apply {
            insert(presets)
        }
    }

    @Test
    fun givenValidPresetIdWhenGetThenReturnPreset() = runBlockingTest {
        assertThat(dao.get(presets[0].id)).isNotNull()
    }

    @Test
    fun givenInvalidPresetIdWhenGetThenReturnNull() = runBlockingTest {
        assertThat(dao.get(0)).isNull()
    }

    @Test
    fun whenGetAllByNameAscendingAsLiveDataThenLiveDataSortedByNameAscending() {
        assertThat(dao.getAllByNameAscendingAsLiveData().apply {
            observeForever {}
        }.value).containsExactlyElementsIn(presets.sortedBy { it.name })
    }

    @Test
    fun givenValidPresetIdWhenDeleteByIdThenDeleteFromDatabase() = runBlockingTest {
        with(dao) {
            delete(presets[0].id)
            assertThat(get(presets[0].id)).isNull()
        }
    }

    @Test
    fun givenValidPresetIdsWhenDeleteAllByIdsThenDeleteFromDatabase() = runBlockingTest {
        with(dao) {
            val ids = presets.subList(0, 4).map { it.id }.toSet()
            delete(ids)
            ids.forEach {
                assertThat(get(it)).isNull()
            }
        }
    }
}