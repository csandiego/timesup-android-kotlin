package com.github.csandiego.timesup.junit

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.reflect.KClass

class RoomDatabaseRule<T : RoomDatabase>(
    private val context: Context,
    private val clazz: Class<T>
) : TestWatcher() {

    constructor(context: Context, klass: KClass<T>) : this(context, klass.java)

    lateinit var database: T

    override fun finished(description: Description?) {
        super.finished(description)
        database.close()
    }

    override fun starting(description: Description?) {
        super.starting(description)
        database = Room.inMemoryDatabaseBuilder(context, clazz)
            .allowMainThreadQueries()
            .build()
    }
}