package com.github.csandiego.timesup

import com.github.csandiego.timesup.dagger.DaggerTestApplicationComponent
import com.github.csandiego.timesup.room.TimesUpDatabase
import javax.inject.Inject

class TestTimesUpApplication : TimesUpApplication() {

    @Inject
    lateinit var database: TimesUpDatabase

    override fun initDagger() {
        DaggerTestApplicationComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}