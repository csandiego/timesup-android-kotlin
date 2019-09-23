package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TimesUpApplication
import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, MainActivityInjectorModule::class])
interface ApplicationComponent {

    fun inject(application: TimesUpApplication)
}