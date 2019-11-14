package com.github.csandiego.timesup.editor

import androidx.lifecycle.*
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.PresetRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

open class PresetEditorViewModel @Inject constructor(private val repository: PresetRepository)
    : ViewModel() {

    private val preset = MediatorLiveData<Preset?>().apply { value = Preset() }
    
    private val _name = MediatorLiveData<String>().apply { 
        addSource(preset) {
            if (it != null && value != it.name) {
                value = it.name
            }
        }
    }.also {
        with(preset) {
            addSource(it) { name ->
                value?.let {
                    if (it.name != name) {
                        value = it.copy(name = name)
                    }
                }
            }
        }
    }
    val name: MutableLiveData<String> = _name

    private val _hours = MediatorLiveData<Int>().apply {
        addSource(preset) {
            if (it != null && value != it.hours) {
                value = it.hours
            }
        }
    }.also {
        with(preset) {
            addSource(it) { hours ->
                value?.let {
                    if (it.hours != hours) {
                        value = it.copy(hours = hours)
                    }
                }
            }
        }
    }
    val hours: MutableLiveData<Int> = _hours

    private val _minutes = MediatorLiveData<Int>().apply {
        addSource(preset) {
            if (it != null && value != it.minutes) {
                value = it.minutes
            }
        }
    }.also {
        with(preset) {
            addSource(it) { minutes ->
                value?.let {
                    if (it.minutes != minutes) {
                        value = it.copy(minutes = minutes)
                    }
                }
            }
        }
    }
    val minutes: MutableLiveData<Int> = _minutes

    private val _seconds = MediatorLiveData<Int>().apply {
        addSource(preset) {
            if (it != null && value != it.seconds) {
                value = it.seconds
            }
        }
    }.also {
        with(preset) {
            addSource(it) { seconds ->
                value?.let {
                    if (it.seconds != seconds) {
                        value = it.copy(seconds = seconds)
                    }
                }
            }
        }
    }
    val seconds: MutableLiveData<Int> = _seconds

    fun load(presetId: Long) {
        with(preset) {
            value?.let {
                if (it.id != presetId) {
                    addSource(repository.getAsLiveData(presetId)) {
                        value = it
                    }
                }
            }
        }
    }

    private val isValid = MediatorLiveData<Boolean>().apply {
        value = false
        addSource(preset) {
            val valid = it?.run {
                name.isNotEmpty() && hours + minutes + seconds > 0
            } ?: false
            if (value != valid) {
                value = valid
            }
        }
    }
    val showSaveButton: LiveData<Boolean> = isValid

    open fun save() {
        if (isValid.value == true) {
            preset.value?.let {
                viewModelScope.launch {
                    repository.save(it)
                }
            }
        }
    }
}