package com.github.csandiego.timesup.timer

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import javax.inject.Inject
import kotlin.math.roundToLong

class TimerViewModel @Inject constructor(private val repository: PresetRepository) : ViewModel() {

    private val _startButtonEnabled = MutableLiveData(false)
    val startButtonEnabled: LiveData<Boolean> get() = _startButtonEnabled

    private val _pauseButtonEnabled = MutableLiveData(false)
    val pauseButtonEnabled: LiveData<Boolean> get() = _pauseButtonEnabled

    private val _resetButtonEnabled = MutableLiveData(false)
    val resetButtonEnabled: LiveData<Boolean> get() = _resetButtonEnabled

    private var presetId = 0L
    private val _preset = MediatorLiveData<Preset?>().apply { value = Preset() }
    val preset: LiveData<Preset?> get() = _preset

    fun load(presetId: Long) {
        if (this.presetId != presetId) {
            this.presetId = presetId
            with(_preset) {
                addSource(repository.getAsLiveData(presetId)) {
                    value = it
                }
            }
        }
    }

    private val _timeLeft = MediatorLiveData<Long>().apply {
        addSource(_preset) {
            it?.let {
                _startButtonEnabled.value = true
                _pauseButtonEnabled.value = false
                _resetButtonEnabled.value = false
                value = it.hours * 60L * 60L + it.minutes * 60L + it.seconds
            }
        }
    }
    val timeLeft = _timeLeft.map {
        val hours = it / (60L * 60L)
        val rem = it % (60L * 60L)
        val minutes = rem / 60L
        val seconds = rem % 60L
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private var timer: CountDownTimer? = null

    fun start() {
        _startButtonEnabled.value = false
        _pauseButtonEnabled.value = true
        _resetButtonEnabled.value = false
        timer = object : CountDownTimer(_timeLeft.value!! * 1000L, 1000L) {

            override fun onFinish() {
                _startButtonEnabled.value = false
                _pauseButtonEnabled.value = false
                _resetButtonEnabled.value = true
                _timeLeft.value = 0L
                timer = null
            }

            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = (millisUntilFinished.toDouble() / 1000.0).roundToLong()
            }
        }.start()
    }

    fun pause() {
        _startButtonEnabled.value = true
        _pauseButtonEnabled.value = false
        _resetButtonEnabled.value = true
        timer?.cancel()
        timer = null
    }

    fun reset() {
        _startButtonEnabled.value = true
        _pauseButtonEnabled.value = false
        _resetButtonEnabled.value = false
        with(_preset.value!!) {
            _timeLeft.value = hours * 60L * 60L + minutes * 60L + seconds
        }
    }
}