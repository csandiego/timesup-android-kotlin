package com.github.csandiego.timesup.timer

class TestCurrentTimeProvider : CurrentTimeProvider {

    var currentTime = 0L

    override fun currentTimeMillis() = currentTime++
}