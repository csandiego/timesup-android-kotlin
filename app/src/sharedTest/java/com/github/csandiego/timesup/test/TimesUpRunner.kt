package com.github.csandiego.timesup.test

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.TimesUpApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TimesUpRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        val name = if (className == TimesUpApplication::class.java.name) {
            TestTimesUpApplication::class.java.name
        } else {
            className
        }
        return super.newApplication(cl, name, context)
    }
}