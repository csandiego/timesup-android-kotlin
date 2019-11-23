package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.timer.ManualTimer
import com.github.csandiego.timesup.timer.Timer
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface TestTimerModule {

    @Singleton
    @Binds
    fun provideTimer(timer: ManualTimer): Timer
}