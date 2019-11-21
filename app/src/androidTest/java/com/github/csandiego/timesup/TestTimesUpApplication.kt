package com.github.csandiego.timesup

import com.github.csandiego.timesup.dagger.DaggerTestApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class TestTimesUpApplication : TimesUpApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerTestApplicationComponent.factory().create(this)
    }
}