package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TestTimesUpApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ApplicationModule::class,
    TestRoomModule::class,
    TestCoroutineScopeModule::class
])
interface TestApplicationComponent : ApplicationComponent {

    fun inject(application: TestTimesUpApplication)

    @Component.Builder
    interface Builder : ApplicationComponent.Builder
}