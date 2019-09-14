package com.github.csandiego.timesup.presets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData
import com.github.csandiego.timesup.repository.PresetRepository

class PresetsViewModel(
    application: Application,
    private val repository: PresetRepository
) : AndroidViewModel(application) {

    val presets = repository.getAllByNameAscendingAsDataSourceFactory().toLiveData(10)
}