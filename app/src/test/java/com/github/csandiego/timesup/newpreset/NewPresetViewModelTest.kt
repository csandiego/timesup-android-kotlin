package com.github.csandiego.timesup.newpreset

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
class NewPresetViewModelTest {

    private val preset = Preset(1, "3.5 hours and 10 seconds", 3, 30, 10)

    private lateinit var database: TimesUpDatabase
    private lateinit var repository: DefaultPresetRepository
    private lateinit var viewModel: NewPresetViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(application, TimesUpDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = DefaultPresetRepository(database.presetDao(), TestCoroutineScope())
        viewModel = NewPresetViewModel(application, repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun whenLoadedThenHideSaveButton() {
        assertThat(viewModel.showSaveButton.value).isFalse()
    }

    @Test
    fun whenNameEnteredAndDurationEmptyThenSaveButtonStaysHidden() {
        val observer = object : Observer<Boolean> {
            var calls = 0
            override fun onChanged(t: Boolean?) {
                ++calls
            }
        }
        var calls = 0
        with (viewModel) {
            showSaveButton.observeForever(observer)
            calls = observer.calls
            name = preset.name
            assertThat(showSaveButton.value).isFalse()
        }
        assertThat(observer.calls).isEqualTo(calls)
    }

    @Test
    fun whenNameEmptyAndDurationEnteredThenSaveButtonStaysHidden() {
        val observer = object : Observer<Boolean> {
            var calls = 0
            override fun onChanged(t: Boolean?) {
                ++calls
            }
        }
        var calls = 0
        with (viewModel) {
            showSaveButton.observeForever(observer)
            calls = observer.calls
            hours = preset.hours
            assertThat(showSaveButton.value).isFalse()
        }
        assertThat(observer.calls).isEqualTo(calls)
    }

    @Test
    fun whenNameAndDurationEnteredThenShowSaveButton() {
        with (viewModel) {
            name = preset.name
            hours = preset.hours
            minutes = preset.minutes
            seconds = preset.seconds
            assertThat(showSaveButton.value).isTrue()
        }
    }

    @Test
    fun givenSaveButtonHiddenWhenCreateThenNoNotUpdateRepository() {
        viewModel.create()
        runBlockingTest {
            assertThat(repository.get(preset.id)).isNull()
        }
    }

    @Test
    fun givenSaveButtonShownWhenCreateThenUpdateRepository() {
        with (viewModel) {
            name = preset.name
            hours = preset.hours
            minutes = preset.minutes
            seconds = preset.seconds
            create()
        }
        runBlockingTest {
            assertThat(repository.get(preset.id)).isEqualTo(preset)
        }
    }
}