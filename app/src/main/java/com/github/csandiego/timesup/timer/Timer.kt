package com.github.csandiego.timesup.timer

import androidx.lifecycle.LiveData
import com.github.csandiego.timesup.data.Preset

interface Timer {

    enum class State {
        INITIAL,
        LOADED,
        STARTED,
        PAUSED,
        FINISHED
    }

    val state: LiveData<State>
    val preset: LiveData<Preset>
    val timeLeft: LiveData<Long>
    val showNotification: LiveData<Boolean>
    fun showNotificationHandled()

    fun load(preset: Preset)
    fun start()
    fun pause()
    fun reset()
    fun clear()
}