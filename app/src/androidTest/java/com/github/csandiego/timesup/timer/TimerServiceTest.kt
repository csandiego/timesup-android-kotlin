package com.github.csandiego.timesup.timer

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.test.assertThatNotificationText
import com.github.csandiego.timesup.test.assertThatNotificationTitle
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class TimerServiceTest {

    private lateinit var device: UiDevice
    private lateinit var timer: TestTimer
    private val testPreset = Preset(id = 1L, name = "3 seconds", seconds = 3)

    @get:Rule
    val serviceTestRule = ServiceTestRule()

    @Before
    fun setUp() = runBlocking<Unit> {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val app = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().apply {
            timer = dagger.timer().apply {
                loadInBackground(testPreset)
                startInBackground()
            }
        }
        val intent = Intent(app, TimerService::class.java)
        serviceTestRule.startService(intent)
        device.openNotification()
    }

    @After
    fun tearDown() {
        device.pressBack()
    }

    @Test
    @Ignore("Foreground service notification UI changed in Android 13")
    fun givenTimerIsInStartedStateWhenServiceStartedThenShowNotification() {
        assertThatNotificationTitle().isEqualTo(testPreset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(testPreset.duration))
    }

    @Test
    @Ignore("Foreground service notification UI changed in Android 13")
    fun givenServiceStartedWhenOneSecondPassedThenUpdateNotification() {
        val advance = 1L
        timer.advanceInBackgroundBy(advance)
        assertThatNotificationText()
            .isEqualTo(DurationFormatter.format(testPreset.duration - advance))
    }
}