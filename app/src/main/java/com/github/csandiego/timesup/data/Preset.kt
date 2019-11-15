package com.github.csandiego.timesup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Preset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String = "",
    var hours: Int = 0,
    var minutes: Int = 0,
    var seconds: Int = 0
) {
    val duration: Long get() = hours * 60L * 60L + minutes * 60L + seconds
}