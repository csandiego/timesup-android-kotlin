package com.github.csandiego.timesup.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.csandiego.timesup.data.Preset

@Database(entities = [Preset::class], version = 1)
abstract class TimesUpDatabase : RoomDatabase() {

    abstract fun presetDao(): PresetDao

    companion object {
        private var instance: TimesUpDatabase? = null

        fun getInstance(context: Context): TimesUpDatabase {
            val currentInstance = instance
            if (currentInstance != null)
                return currentInstance

            return synchronized(this) {
                Room.databaseBuilder(context, TimesUpDatabase::class.java, "TimesUp")
                    .build()
                    .also {
                        instance = it
                    }
            }
        }
    }
}