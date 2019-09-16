package com.github.csandiego.timesup.newpreset

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository

class NewPresetViewModel(
    application: Application,
    private val repository: PresetRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        DefaultPresetRepository.getInstance(application)
    )

    private val preset = Preset()
    var name: String
        get() = preset.name
        set(value) {
            preset.name = value
            validate()
        }
    var hours: Int
        get() = preset.hours
        set(value) {
            preset.hours = value
            validate()
        }
    var minutes: Int
        get() = preset.minutes
        set(value) {
            preset.minutes = value
            validate()
        }
    var seconds: Int
        get() = preset.seconds
        set(value) {
            preset.seconds = value
            validate()
        }

    private val isValid = MutableLiveData(false)
    val showSaveButton: LiveData<Boolean> = isValid

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