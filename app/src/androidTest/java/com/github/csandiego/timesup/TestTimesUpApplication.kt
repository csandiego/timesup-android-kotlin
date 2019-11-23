package com.github.csandiego.timesup

import com.github.csandiego.timesup.dagger.DaggerTestApplicationComponent
import com.github.csandiego.timesup.dagger.TestApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class TestTimesUpApplication : TimesUpApplication() {

    lateinit var dagger: TestApplicationComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerTestApplicationComponent.factory().create(this).also {
            dagger = it as TestApplicationComponent
        }
    }
}