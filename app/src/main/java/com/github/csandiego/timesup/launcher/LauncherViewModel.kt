package com.github.csandiego.timesup.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository

class LauncherViewModel(
    application: Application,
    private val repository: PresetRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        DefaultPresetRepository.getInstance(application)
    )

    private var presetId: Long? = null
    private lateinit var _preset: LiveData<Preset?>
    val preset get() = _preset

    fun load(presetId: Long) {
        if (this.presetId == presetId)
            return

        this.presetId = presetId
        _preset = repository.getAsLiveData(presetId)
    }
}