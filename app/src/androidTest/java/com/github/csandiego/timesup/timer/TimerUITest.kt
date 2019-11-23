package com.github.csandiego.timesup.timer

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.data.Preset
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerUITest {

    private lateinit var timer: TestTimer
    private val preset = Preset(id = 1L, name = "3 seconds", seconds = 3)
    private lateinit var device: UiDevice
    private lateinit var app: TestTimesUpApplication

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() = runBlocking<Unit> {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        app = ApplicationProvider.getApplicationContext<TestTimesUpApplication>().apply {
            with(dagger) {
                database().presetDao().insert(preset)
                timer = timer()
            }
        }
        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @Test
    fun givenViewDisplayedWhenUpButtonPressedThenNavigateToPresets() {
        onView(
            allOf(
                withParent(withId(R.id.toolbar)),
                withContentDescription(R.string.abc_action_bar_up_description)
            )
        ).perform(click())
        onView(
            allOf(
                withParent(withId(R.id.toolbar)),
                withText(app.getString(R.string.app_name))
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun givenTimerIsInStartedStateWhenExpiredThenShowNotification() {
        activityScenarioRule.scenario.onActivity {
            with(timer) {
                start()
                advanceBy(this@TimerUITest.preset.duration)
            }
        }
        device.openNotification()
        val notification = device.findObject(By.hasChild(
            By.res("android:id/notification_header").hasChild(
                By.res("android:id/app_name_text").text(app.getString(R.string.app_name))
            )
        ))
        try {
            assertThat(notification?.findObject(By.res("android:id/title"))?.text)
                .isEqualTo(preset.name)
            assertThat(notification?.findObject(By.res("android:id/text"))?.text)
                .isEqualTo(DurationFormatter.format(0L))
        } catch (e: Throwable) {
            device.pressBack()
            throw e
        }
        device.pressBack()
    }
}