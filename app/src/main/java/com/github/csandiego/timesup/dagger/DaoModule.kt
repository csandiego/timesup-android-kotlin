package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.room.TimesUpDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoModule {

    @Singleton
    @Provides
    fun providePresetDao(database: TimesUpDatabase) = database.presetDao()
}