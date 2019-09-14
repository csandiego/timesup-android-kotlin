package com.github.csandiego.timesup.newpreset

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository

class NewPresetViewModel(
    application: Application,
    private val repository: PresetRepository
) : AndroidViewModel(application) {

    private val preset = Preset()
    val name = MutableLiveData(preset.name).apply { observeForever { preset.name = it } }
    val hours = MutableLiveData(preset.hours).apply { observeForever { preset.hours = it } }
    val minutes = MutableLiveData(preset.minutes).apply { observeForever { preset.minutes = it } }
    val seconds = MutableLiveData(preset.seconds).apply { observeForever { preset.seconds = it } }
    private val isValid = MutableLiveData(false)
    val showSaveButton: LiveData<Boolean> = isValid

    init {
        name.observeForever { validate() }
        hours.observeForever { validate() }
        minutes.observeForever { validate() }
        seconds.observeForever { validate() }
    }

    private fun validate() {
        val valid = preset.run {
            name.isNotEmpty() && hours + minutes + seconds > 0
        }
        if (isValid.value != valid) {
            isValid.value = valid
        }
    }

    fun create() {
        isValid.value?.let {
            if (it) {
                repository.create(preset)
            }
        }
    }
}