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
        with (_selection) {
            value = value?.let {
                if (it.contains(preset)) {
                    it - preset
                } else {
                    it + preset
                }
            } ?: setOf(preset)
        }
    }

    fun clearSelection() {
        with(_selection) {
            if (value?.isNotEmpty() == true) {
                value = emptySet()
            }
        }
    }

    fun deleteSelected() {
        with(_selection) {
            val selection = value
            if (selection.isNullOrEmpty()) {
                return
            }
            value = emptySet()
            repository.deleteAll(selection.toList())
        }
    }
}