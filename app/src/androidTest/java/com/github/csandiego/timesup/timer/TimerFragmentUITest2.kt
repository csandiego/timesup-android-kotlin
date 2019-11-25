package com.github.csandiego.timesup.timer

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.assertThatNotificationText
import com.github.csandiego.timesup.test.assertThatNotificationTitle
import com.github.csandiego.timesup.test.findNotification
import com.github.csandiego.timesup.test.findNotificationPanel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class TimerFragmentUITest2 {

    private lateinit var device: UiDevice
    private lateinit var scenario: FragmentScenario<TimerFragment>
    private lateinit var timer: TestTimer
    private val testPreset = Preset(id = 1L, name = "2 seconds", seconds = 2)

    @Before
    fun setUp() = runBlocking {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val dagger = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().dagger
        dagger.dao().insert(testPreset)
        timer = dagger.timer()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TimerViewModel(dagger.repository(), timer) as T
            }
        }
        val args = Bundle().apply {
            putLong("presetId", testPreset.id)
        }
        scenario = launchFragmentInContainer(args, R.style.Theme_TimesUp) {
            TimerFragment(viewModelFactory)
        }
    }

    @After
    fun tearDown() {
        if (findNotificationPanel() != null) {
            device.pressBack()
        }
    }

    @Test
    fun givenValidPresetIdWhenFragmentCreatedThenLoadTimerUsingPreset() {
        scenario.onFragment {
            assertThat(timer.preset.value).isEqualTo(testPreset)
        }
    }

    @Test
    fun givenTimerIsInStartedStateWhenFragmentStoppedThenStartForegroundService() {
        scenario.onFragment {
            timer.start()
        }.moveToState(Lifecycle.State.CREATED)
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(testPreset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(testPreset.duration))
    }

    @Test
    fun givenTimerIsInPausedStateWhenFragmentStoppedThenStartForegroundService() {
        val advance = 1L
        scenario.onFragment {
            with(timer) {
                start()
                advanceBy(advance)
                pause()
            }
        }.moveToState(Lifecycle.State.CREATED)
        device.openNotification()
        assertThatNotificationTitle().isEqualTo(testPreset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(testPreset.duration - advance))
    }

    @Test
    fun givenForegroundStartedWhenFragmentStartedThenStopForegroundService() {
        scenario.onFragment {
            timer.start()
        }.moveToState(Lifecycle.State.CREATED).moveToState(Lifecycle.State.RESUMED)
        device.openNotification()
        assertThat(findNotification()).isNull()
        device.pressBack()
        scenario.onFragment {
            timer.pause()
        }.moveToState(Lifecycle.State.CREATED).moveToState(Lifecycle.State.RESUMED)
        device.openNotification()
        assertThat(findNotification()).isNull()
    }
}