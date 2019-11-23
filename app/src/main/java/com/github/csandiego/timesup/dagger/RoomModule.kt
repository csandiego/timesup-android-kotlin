package com.github.csandiego.timesup.dagger

import android.content.Context
import androidx.room.Room
import com.github.csandiego.timesup.room.TimesUpDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): TimesUpDatabase {
        return Room.databaseBuilder(context, TimesUpDatabase::class.java, "TimesUp")
            .build()
    }
}