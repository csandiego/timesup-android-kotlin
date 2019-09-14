package com.github.csandiego.timesup.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PresetDaoTest {

    private val presets = listOf(
        Preset(1, "1 minute", 0, 1, 0),
        Preset(2, "2.5 minutes", 0, 2, 30),
        Preset(3, "5 minutes", 0, 5, 0),
        Preset(4, "1 hour", 1, 0, 0),
        Preset(5, "1.5 hours", 1, 30, 0)
    )

    private lateinit var database: TimesUpDatabase
    private lateinit var dao: PresetDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TimesUpDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.presetDao().apply {
            runBlockingTest {
                insertAll(presets)
            }
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenValidPresetIdWhenGetThenReturnPreset() {
        runBlockingTest {
            assertThat(dao.get(presets[0].id)).isNotNull()
        }
    }

    @Test
    fun givenInvalidPresetIdWhenGetThenReturnNull() {
        runBlockingTest {
            assertThat(dao.get(0)).isNull()
        }
    }
}