package com.github.csandiego.timesup.espresso

import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object ViewMatchers {
    fun isActivated(): Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun matchesSafely(item: View) = item.isActivated

        override fun describeTo(description: Description) {
            description.appendText("activated")
        }
    }
}