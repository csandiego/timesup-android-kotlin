package com.github.csandiego.timesup.test

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.reflect.KClass

class RoomDatabaseRule<T : RoomDatabase>(
    private val context: Context,
    private val clazz: Class<T>,
    private val allowMainThreadQueries: Boolean = false
) : TestWatcher() {

    constructor(context: Context, klass: KClass<T>, allowMainThreadQueries: Boolean = false) : this(
        context,
        klass.java,
        allowMainThreadQueries
    )

    lateinit var database: T

    override fun finished(description: Description?) {
        super.finished(description)
        database.close()
    }

    override fun starting(description: Description?) {
        super.starting(description)
        database = Room.inMemoryDatabaseBuilder(context, clazz).apply {
            if (allowMainThreadQueries) {
                allowMainThreadQueries()
            }
        }.build()
    }
}