package com.github.csandiego.timesup.presets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository

class PresetsViewModel(
    application: Application,
    private val repository: PresetRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        DefaultPresetRepository.getInstance(application)
    )

    val presets = repository.getAllByNameAscendingAsLiveData()

    fun delete(preset: Preset) {
        repository.delete(preset)
    }

    private val _selection = MutableLiveData<Set<Preset>>()
    val selection: LiveData<Set<Preset>> = _selection

    fun toggleSelect(preset: Preset) {
        _selection.value = _selection.value?.let {
            if (it.contains(preset)) {
                it - preset
            } else {
                it + preset
            }
        } ?: setOf(preset)
    }

    fun clearSelection() {
        _selection.value?.run {
            if (isNotEmpty()) {
                _selection.value = emptySet()
            }
        }
    }
}