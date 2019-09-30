package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TestTimesUpApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    TestApplicationModule::class,
    TestRoomModule::class,
    TestCoroutineScopeModule::class
])
interface TestApplicationComponent : AndroidInjector<TestTimesUpApplication> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<TestTimesUpApplication>
}