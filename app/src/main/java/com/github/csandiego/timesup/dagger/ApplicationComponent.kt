package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TimesUpApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ApplicationModule::class,
    RoomModule::class,
    TimerModule::class
])
interface ApplicationComponent : AndroidInjector<TimesUpApplication> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<TimesUpApplication>
}