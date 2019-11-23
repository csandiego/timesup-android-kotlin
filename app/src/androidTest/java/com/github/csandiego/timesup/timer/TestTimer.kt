package com.github.csandiego.timesup.timer

import com.github.csandiego.timesup.data.Preset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestTimer @Inject constructor() : ManualTimer() {

    fun loadInBackground(preset: Preset) {
        loadInternal(preset, true)
    }

    fun startInBackground() {
        startInternal(true)
    }

    fun pauseInBackground() {
        pauseInternal(true)
    }

    fun resetInBackground() {
        resetInternal(true)
    }

    fun clearInBackground() {
        clearInternal(true)
    }

    fun advanceInBackgroundBy(seconds: Long) {
        advanceBy(seconds, true)
    }
}