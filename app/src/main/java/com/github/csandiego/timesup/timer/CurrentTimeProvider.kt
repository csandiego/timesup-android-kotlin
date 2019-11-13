package com.github.csandiego.timesup.timer

interface CurrentTimeProvider {

    fun currentTimeMillis(): Long
}