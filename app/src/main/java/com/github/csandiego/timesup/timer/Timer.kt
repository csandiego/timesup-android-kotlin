package com.github.csandiego.timesup.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.github.csandiego.timesup.data.Preset
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

@Singleton
class Timer @Inject constructor(private val currentTimeProvider: CurrentTimeProvider) {

    enum class State {
        INITIAL,
        LOADED,
        STARTED,
        PAUSED,
        FINISHED
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

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

    fun load(preset: Preset) {
        check(_state.value == State.INITIAL) { "Loading outside of initial state" }
        _preset.value = preset
        _timeLeft.value = preset.run { hours * 60L * 60L + minutes * 60L + seconds }
        _state.value = State.LOADED
    }

    private val _showNotification = MutableLiveData(false)
    val showNotification: LiveData<Boolean> get() = _showNotification

    fun showNotificationHandled() {
        _showNotification.value = false
    }

    fun start() {
        check(
            setOf(
                State.LOADED,
                State.PAUSED
            ).contains(_state.value)
        ) { "Starting outside of loaded state" }
        job = coroutineScope.launch {
            val duration = _timeLeft.value!! * 1000L
            launch {
                timeFlow(duration, 1000L)
                    .collect {
                        _timeLeft.value = ((it.toDouble() / 1000.0).roundToLong())
                    }
            }
            launch {
                delay(duration)
                _timeLeft.value = 0L
                _showNotification.value = true
                _state.value = State.FINISHED
            }
        }

        _state.value = State.STARTED
    }

    fun pause() {
        check(_state.value == State.STARTED) { "Pausing outside of started state" }
        job!!.cancel()
        job = null
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
        job?.cancel()
        job = null
        _state.value = State.INITIAL
        _preset.value = null
        _timeLeft.value = null
        _showNotification.value = false
    }

    private fun timeFlow(duration: Long, interval: Long): Flow<Long> = flow {
        val start = currentTimeProvider.currentTimeMillis()
        var next = start
        while (currentTimeProvider.currentTimeMillis() < start + duration) {
            emit(start + duration - currentTimeProvider.currentTimeMillis())
            next += interval
            delay(next - currentTimeProvider.currentTimeMillis())
        }
    }
}