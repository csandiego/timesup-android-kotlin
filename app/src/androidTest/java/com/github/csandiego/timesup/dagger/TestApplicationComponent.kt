package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TestTimesUpApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    TestApplicationModule::class,
    TestRoomModule::class
])
interface TestApplicationComponent : AndroidInjector<TestTimesUpApplication> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<TestTimesUpApplication>
}