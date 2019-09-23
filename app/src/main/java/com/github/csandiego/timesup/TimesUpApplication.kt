package com.github.csandiego.timesup

import android.app.Application
import com.github.csandiego.timesup.dagger.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TimesUpApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = injector
}