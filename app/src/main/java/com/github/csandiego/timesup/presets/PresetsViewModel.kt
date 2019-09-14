package com.github.csandiego.timesup.presets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData
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

    val presets = repository.getAllByNameAscendingAsDataSourceFactory().toLiveData(10)
}