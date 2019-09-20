package com.github.csandiego.timesup.presets

import com.github.csandiego.timesup.data.Preset

interface PresetsItemCallback {

    fun onPresetClick(preset: Preset)

    fun onPresetLongClick(preset: Preset): Boolean
}