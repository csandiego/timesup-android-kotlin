package com.github.csandiego.timesup.dagger

import android.app.Application
import android.content.Context
import com.github.csandiego.timesup.MainActivity
import com.github.csandiego.timesup.TimesUpApplication
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.timer.*
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
interface ApplicationModule {

    @Singleton
    @Binds
    fun provideContext(application: TimesUpApplication): Context

    @Singleton
    @Binds
    fun provideApplication(application: TimesUpApplication): Application

    @Singleton
    @Binds
    fun providePresetRepository(repository: DefaultPresetRepository): PresetRepository

    @Singleton
    @Binds
    fun provideCurrentTimeProvider(currentTimeProvider: DefaultCurrentTimeProvider): CurrentTimeProvider

    @Singleton
    @Binds
    fun provideTimer(timer: DefaultTimer): Timer

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun contributeMainActivityAndroidInjector(): MainActivity

    @ServiceScope
    @ContributesAndroidInjector
    fun contributeTimerServiceInjector(): TimerService
}