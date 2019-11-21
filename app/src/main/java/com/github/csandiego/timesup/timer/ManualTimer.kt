package com.github.csandiego.timesup.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset

open class ManualTimer : Timer {

    private val _state = MutableLiveData(Timer.State.INITIAL)
    override val state: LiveData<Timer.State> = _state

    private val _preset = MediatorLiveData<Preset>()
    override val preset: LiveData<Preset> = _preset

    private val _timeLeft = MutableLiveData<Long>()
    override val timeLeft: LiveData<Long> = _timeLeft

    private val _showNotification = MutableLiveData(false)
    override val showNotification: LiveData<Boolean> = _showNotification

    override fun showNotificationHandled() {
        _showNotification.value = false
    }

    override fun load(preset: Preset) {
        check(_state.value == Timer.State.INITIAL)
        _preset.value = preset
        _timeLeft.value = preset.duration
        _state.value = Timer.State.LOADED
    }

    override fun start() {
        check(setOf(Timer.State.LOADED, Timer.State.PAUSED).contains(_state.value))
        _state.value = Timer.State.STARTED
    }

    override fun pause() {
        check(_state.value == Timer.State.STARTED)
        _state.value = Timer.State.PAUSED
    }

    override fun reset() {
        check(setOf(Timer.State.PAUSED, Timer.State.FINISHED).contains(_state.value))
        _timeLeft.value = _preset.value!!.duration
        _state.value = Timer.State.LOADED
    }

    override fun clear() {
        _state.value = Timer.State.INITIAL
        _preset.value = null
        _timeLeft.value = null
        _showNotification.value = false
    }

    fun advanceBy(seconds: Long) {
        check(_state.value == Timer.State.STARTED)
        val remaining = _timeLeft.value!! - seconds
        if (remaining > 0) {
            _timeLeft.value = remaining
        } else {
            _timeLeft.value = 0L
            _showNotification.value = true
            _state.value = Timer.State.FINISHED
        }
    }
}