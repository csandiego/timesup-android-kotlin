package com.github.csandiego.timesup

import com.github.csandiego.timesup.dagger.DaggerTestApplicationComponent
import com.github.csandiego.timesup.room.TimesUpDatabase
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class TestTimesUpApplication : TimesUpApplication() {

    @Inject
    lateinit var database: TimesUpDatabase

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerTestApplicationComponent.factory().create(this)
    }
}