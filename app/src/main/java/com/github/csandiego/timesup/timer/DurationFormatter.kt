package com.github.csandiego.timesup.timer

object DurationFormatter {

    @JvmStatic
    fun format(duration: Long): String {
        val hours = duration / 60L / 60L
        val rem = duration % (60L * 60L)
        val minutes = rem / 60L
        val seconds = rem % 60L
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}