package com.github.csandiego.timesup.timer

import javax.inject.Inject

class DefaultCurrentTimeProvider @Inject constructor() : CurrentTimeProvider {

    override fun currentTimeMillis() = System.currentTimeMillis()
}