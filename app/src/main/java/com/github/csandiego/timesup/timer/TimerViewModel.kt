package com.github.csandiego.timesup.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import javax.inject.Inject

class TimerViewModel @Inject constructor(val timer: Timer) : ViewModel() {

    val startButtonEnabled = timer.state.map {
        setOf(Timer.State.LOADED, Timer.State.PAUSED).contains(it)
    }
    val pauseButtonEnabled = timer.state.map {
        it == Timer.State.STARTED
    }
    val resetButtonEnabled = timer.state.map {
        setOf(Timer.State.PAUSED, Timer.State.FINISHED).contains(it)
    }
}