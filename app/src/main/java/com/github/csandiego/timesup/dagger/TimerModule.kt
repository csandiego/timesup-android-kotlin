package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.timer.CurrentTimeProvider
import com.github.csandiego.timesup.timer.DefaultCurrentTimeProvider
import com.github.csandiego.timesup.timer.DefaultTimer
import com.github.csandiego.timesup.timer.Timer
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface TimerModule {

    @Singleton
    @Binds
    fun provideCurrentTimeProvider(currentTimeProvider: DefaultCurrentTimeProvider): CurrentTimeProvider

    @Singleton
    @Binds
    fun provideTimer(timer: DefaultTimer): Timer
}