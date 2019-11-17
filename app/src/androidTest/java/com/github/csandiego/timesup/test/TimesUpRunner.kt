package com.github.csandiego.timesup.test

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.github.csandiego.timesup.TestTimesUpApplication

class TimesUpRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application = super.newApplication(cl, TestTimesUpApplication::class.java.name, context)
}