package com.github.csandiego.timesup.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import javax.inject.Inject

open class ManualTimer @Inject constructor() : Timer {

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

    protected fun loadInternal(preset: Preset, postToMainThread: Boolean = false) {
        check(_state.value == Timer.State.INITIAL)
        if (postToMainThread) {
            _preset.postValue(preset)
            _timeLeft.postValue(preset.duration)
            _state.postValue(Timer.State.LOADED)
        } else {
            _preset.value = preset
            _timeLeft.value = preset.duration
            _state.value = Timer.State.LOADED
        }
    }

    override fun load(preset: Preset) {
        loadInternal(preset)
    }

    protected fun startInternal(postToMainThread: Boolean = false) {
        check(setOf(Timer.State.LOADED, Timer.State.PAUSED).contains(_state.value))
        if (postToMainThread) {
            _state.postValue(Timer.State.STARTED)
        } else {
            _state.value = Timer.State.STARTED
        }
    }

    override fun start() {
        startInternal()
    }

    protected fun pauseInternal(postToMainThread: Boolean = false) {
        check(_state.value == Timer.State.STARTED)
        if (postToMainThread) {
            _state.postValue(Timer.State.PAUSED)
        } else {
            _state.value = Timer.State.PAUSED
        }
    }

    override fun pause() {
        pauseInternal()
    }

    protected fun resetInternal(postToMainThread: Boolean = false) {
        check(setOf(Timer.State.PAUSED, Timer.State.FINISHED).contains(_state.value))
        if (postToMainThread) {
            _timeLeft.postValue(_preset.value!!.duration)
            _state.postValue(Timer.State.LOADED)
        } else {
            _timeLeft.value = _preset.value!!.duration
            _state.value = Timer.State.LOADED
        }
    }

    override fun reset() {
        resetInternal()
    }

    protected fun clearInternal(postToMainThread: Boolean = false) {
        if (postToMainThread) {
            _state.postValue(Timer.State.INITIAL)
            _preset.postValue(null)
            _timeLeft.postValue(null)
            _showNotification.postValue(false)
        } else {
            _state.value = Timer.State.INITIAL
            _preset.value = null
            _timeLeft.value = null
            _showNotification.value = false
        }
    }

    override fun clear() {
        clearInternal()
    }

    fun advanceBy(seconds: Long, postToMainThread: Boolean = false) {
        check(_state.value == Timer.State.STARTED)
        val remaining = _timeLeft.value!! - seconds
        if (remaining > 0) {
            if (postToMainThread) {
                _timeLeft.postValue(remaining)
            } else {
                _timeLeft.value = remaining
            }
        } else {
            finish(postToMainThread)
        }
    }

    private fun finish(postToMainThread: Boolean = false) {
        if (postToMainThread) {
            _timeLeft.postValue(0L)
            _showNotification.postValue(true)
            _state.postValue(Timer.State.FINISHED)
        } else {
            _timeLeft.value = 0L
            _showNotification.value = true
            _state.value = Timer.State.FINISHED
        }
    }
}