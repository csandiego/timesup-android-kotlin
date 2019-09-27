package com.github.csandiego.timesup.presets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import javax.inject.Inject

class PresetsViewModel @Inject constructor(private val repository: PresetRepository)
    : ViewModel() {

    val presets = repository.getAllByNameAscendingAsLiveData()

    fun delete(preset: Preset) {
        if (_selection.value?.contains(preset) == true) {
            toggleSelect(preset)
        }
        repository.delete(preset)
    }

    private val _selection = MutableLiveData<Set<Preset>>()
    val selection: LiveData<Set<Preset>> = _selection

    private fun toggleSelect(preset: Preset) {
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

    private val _startTimerForPreset = MutableLiveData<Preset?>()
    val startTimerForPreset: LiveData<Preset?> = _startTimerForPreset
    fun startTimerForPresetHandled() {
        _startTimerForPreset.value = null
    }

    fun onClick(preset: Preset) {
        if (_selection.value.isNullOrEmpty()) {
            _startTimerForPreset.value = preset
        } else {
            toggleSelect(preset)
        }
    }

    fun onLongClick(preset: Preset) = if (_selection.value.isNullOrEmpty()) {
        toggleSelect(preset)
        true
    } else {
        false
    }
}