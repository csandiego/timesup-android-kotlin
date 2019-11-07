package com.github.csandiego.timesup.timer

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

@Singleton
class Timer @Inject constructor(private val repository: PresetRepository) {

    enum class State {
        INITIAL,
        LOADED,
        STARTED,
        PAUSED,
        FINISHED
    }

    private val _state = MutableLiveData(State.INITIAL)
    val state: LiveData<State> get() = _state

    private val _preset = MediatorLiveData<Preset>()
    val preset: LiveData<Preset> get() = _preset

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft = _timeLeft.map {
        it?.let {
            val hours = it / (60L * 60L)
            val rem = it % (60L * 60L)
            val minutes = rem / 60L
            val seconds = rem % 60L
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }

    fun load(presetId: Long) {
        check(_state.value == State.INITIAL) { "Loading outside of initial state" }
        _preset.addSource(repository.getAsLiveData(presetId)) {
            it?.let {
                _preset.value = it
                _timeLeft.value = it.hours * 60L * 60L + it.minutes * 60L + it.seconds
                _state.value = State.LOADED
            }
        }
    }

    private val _showNotification = MutableLiveData(false)
    val showNotification: LiveData<Boolean> get() = _showNotification

    fun showNotificationHandled() {
        _showNotification.value = false
    }

    private var timer: CountDownTimer? = null

    fun start() {
        check(
            setOf(
                State.LOADED,
                State.PAUSED
            ).contains(_state.value)
        ) { "Starting outside of loaded state" }
        timer = object : CountDownTimer(_timeLeft.value!! * 1000L, 1000L) {

            override fun onFinish() {
                _timeLeft.value = 0L
                _showNotification.value = true
                timer = null
                _state.value = State.FINISHED
            }

            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = (millisUntilFinished.toDouble() / 1000.0).roundToLong()
            }
        }.start()
        _state.value = State.STARTED
    }

    fun pause() {
        check(_state.value == State.STARTED) { "Pausing outside of started state" }
        timer?.cancel()
        timer = null
        _state.value = State.PAUSED
    }

    fun reset() {
        check(
            setOf(
                State.PAUSED,
                State.FINISHED
            ).contains(_state.value)
        ) { "Resetting outside of paused/finished state" }
        _timeLeft.value = _preset.value!!.run {
            hours * 60L * 60L + minutes * 60L + seconds
        }
        _state.value = State.LOADED
    }

    fun clear() {
        timer?.cancel()
        timer = null
        _state.value = State.INITIAL
        _preset.value = null
        _timeLeft.value = null
        _showNotification.value = false
    }
}