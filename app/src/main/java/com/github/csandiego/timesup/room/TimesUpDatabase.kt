package com.github.csandiego.timesup.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.csandiego.timesup.data.Preset

@Database(entities = [Preset::class], version = 1, exportSchema = false)
abstract class TimesUpDatabase : RoomDatabase() {

    abstract fun presetDao(): PresetDao
}