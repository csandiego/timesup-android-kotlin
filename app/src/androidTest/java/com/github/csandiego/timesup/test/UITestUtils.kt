package com.github.csandiego.timesup.test

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.room.PresetDao
import com.github.csandiego.timesup.timer.DurationFormatter
import com.google.common.truth.StringSubject
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith

suspend fun PresetDao.insertAndReturnWithId(preset: Preset) = preset.copy(id = insert(preset))

suspend fun PresetDao.insertAndReturnWithId(presets: List<Preset>): List<Preset> =
    mutableListOf<Preset>().apply {
        insert(presets).forEachIndexed { index, id ->
            add(index, presets[index].copy(id = id))
        }
    }

fun assertBound(preset: Preset) {
    onView(withId(R.id.editTextName)).check(matches(withText(preset.name)))
    onView(
        allOf(
            withParent(withId(R.id.numberPickerHours)),
            withClassName(endsWith("CustomEditText"))
        )
    ).check(matches(withText(String.format("%02d", preset.hours))))
    onView(
        allOf(
            withParent(withId(R.id.numberPickerMinutes)),
            withClassName(endsWith("CustomEditText"))
        )
    ).check(matches(withText(String.format("%02d", preset.minutes))))
    onView(
        allOf(
            withParent(withId(R.id.numberPickerSeconds)),
            withClassName(endsWith("CustomEditText"))
        )
    ).check(matches(withText(String.format("%02d", preset.seconds))))
}

private fun pick(id: Int, value: Int) {
    onView(withId(id)).perform(longClick())
    onView(
        allOf(
            withParent(withId(id)),
            withClassName(endsWith("CustomEditText"))
        )
    ).perform(replaceText(value.toString()))
    onView(withId(id)).perform(click())
}

fun fillUpUsing(preset: Preset) {
    onView(withId(R.id.editTextName)).perform(replaceText(preset.name))
    pick(R.id.numberPickerHours, preset.hours)
    pick(R.id.numberPickerMinutes, preset.minutes)
    pick(R.id.numberPickerSeconds, preset.seconds)
}

fun isTheRowFor(preset: Preset): Matcher<View> = allOf(
    withParent(withId(R.id.recyclerView)),
    hasDescendant(
        allOf(
            withId(R.id.textViewName),
            withText(preset.name)
        )
    ),
    hasDescendant(
        allOf(
            withId(R.id.textViewDuration),
            withText(DurationFormatter.format(preset.duration))
        )
    )
)

fun findNotificationPanel(): UiObject2? = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    .findObject(By.res("com.android.systemui:id/notification_panel"))

fun findNotification(): UiObject2? = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    .findObject(
        By.hasChild(
            By.res("android:id/notification_header").hasChild(
                By.res("android:id/app_name_text").text(
                    ApplicationProvider.getApplicationContext<Context>().getString(R.string.app_name)
                )
            )
        )
    )

fun assertThatNotificationTitle(): StringSubject = assertThat(
    findNotification()?.findObject(By.res("android:id/title"))?.text
)

fun assertThatNotificationText(): StringSubject = assertThat(
    findNotification()?.findObject(By.res("android:id/text"))?.text
)