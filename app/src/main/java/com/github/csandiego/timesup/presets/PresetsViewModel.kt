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

    private val _selection = MutableLiveData<Set<Long>>()
    val selection: LiveData<Set<Long>> = _selection

    private fun toggleSelect(presetId: Long) {
        with (_selection) {
            value = value?.let {
                if (it.contains(presetId)) {
                    it - presetId
                } else {
                    it + presetId
                }
            } ?: setOf(presetId)
        }
    }

    fun clearSelection() {
        with(_selection) {
            if (value?.isNotEmpty() == true) {
                value = emptySet()
            }
        }
    }

    fun delete(preset: Preset) {
        if (_selection.value?.contains(preset.id) == true) {
            toggleSelect(preset.id)
        }
        repository.delete(preset.id)
    }

    fun deleteSelected() {
        with(_selection) {
            val selection = value
            if (selection.isNullOrEmpty()) {
                return
            }
            value = emptySet()
            repository.delete(selection)
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
            toggleSelect(preset.id)
        }
    }

    fun onLongClick(preset: Preset) = if (_selection.value.isNullOrEmpty()) {
        toggleSelect(preset.id)
        true
    } else {
        false
    }
}