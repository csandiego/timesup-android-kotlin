package com.github.csandiego.timesup.timer

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerServiceTest {

    private lateinit var timer: TestTimer
    private val preset = Preset(id = 1L, name = "3 seconds", seconds = 3)
    private lateinit var device: UiDevice
    private lateinit var app: TestTimesUpApplication

    @get:Rule
    val serviceTestRule = ServiceTestRule()

    @Before
    fun setUp() = runBlocking<Unit> {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        app = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().apply {
            timer = dagger.timer().apply {
                loadInBackground(this@TimerServiceTest.preset)
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

    private fun findNotification() = device.findObject(
        By.hasChild(
            By.res("android:id/notification_header").hasChild(
                By.res("android:id/app_name_text").text(app.getString(R.string.app_name))
            )
        )
    )

    private fun assertThatNotificationTitle() = assertThat(
        findNotification()?.findObject(By.res("android:id/title"))?.text
    )

    private fun assertThatNotificationText() = assertThat(
        findNotification()?.findObject(By.res("android:id/text"))?.text
    )

    @Test
    fun givenTimerIsInStartedStateWhenServiceStartedThenShowNotification() {
        assertThatNotificationTitle().isEqualTo(preset.name)
        assertThatNotificationText().isEqualTo(DurationFormatter.format(preset.duration))
    }

    @Test
    fun givenServiceStartedWhenOneSecondPassedThenUpdateNotification() {
        val advance = 1L
        timer.advanceInBackgroundBy(advance)
        assertThatNotificationText()
            .isEqualTo(DurationFormatter.format(preset.duration - advance))
    }
}