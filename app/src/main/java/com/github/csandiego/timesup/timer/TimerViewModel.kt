package com.github.csandiego.timesup.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.github.csandiego.timesup.data.Preset
import javax.inject.Inject

class TimerViewModel @Inject constructor() : ViewModel() {

    val state = MutableLiveData(Timer.State.INITIAL)

    val startButtonEnabled = state.map {
        setOf(Timer.State.LOADED, Timer.State.PAUSED).contains(it)
    }
    val pauseButtonEnabled = state.map {
        it == Timer.State.STARTED
    }
    val resetButtonEnabled = state.map {
        setOf(Timer.State.PAUSED, Timer.State.FINISHED).contains(it)
    }

    val preset = MutableLiveData<Preset>()

    val timeLeft = MutableLiveData<String>()

    private val _startTimer = MutableLiveData(false)
    val startTimer: LiveData<Boolean> get() = _startTimer

    fun startTimerHandled() {
        _startTimer.value = false
    }

    fun start() {
        _startTimer.value = true
    }

    private val _pauseTimer = MutableLiveData(false)
    val pauseTimer: LiveData<Boolean> get() = _pauseTimer

    fun pauseTimerHandled() {
        _pauseTimer.value = false
    }

    fun pause() {
        _pauseTimer.value = true
    }

    private val _resetTimer = MutableLiveData(false)
    val resetTimer: LiveData<Boolean> get() = _resetTimer

    fun resetTimerHandled() {
        _resetTimer.value = false
    }

    fun reset() {
        _resetTimer.value = true
    }
}