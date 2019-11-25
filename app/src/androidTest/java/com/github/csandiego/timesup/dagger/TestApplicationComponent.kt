package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.room.PresetDao
import com.github.csandiego.timesup.timer.TestTimer
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    TestApplicationModule::class,
    TestRoomModule::class,
    TestTimerModule::class
])
interface TestApplicationComponent : AndroidInjector<TestTimesUpApplication> {

    fun dao(): PresetDao

    fun repository(): PresetRepository

    fun timer(): TestTimer

    @Component.Factory
    interface Factory : AndroidInjector.Factory<TestTimesUpApplication>
}