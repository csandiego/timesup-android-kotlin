package com.github.csandiego.timesup.dagger

import android.app.Application
import android.content.Context
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ApplicationModule {

    @Singleton
    @Binds
    abstract fun provideContext(application: Application): Context

    @Singleton
    @Binds
    abstract fun providePresetRepository(repository: DefaultPresetRepository): PresetRepository
}