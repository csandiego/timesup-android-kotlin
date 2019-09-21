package com.github.csandiego.timesup.repository

import androidx.lifecycle.LiveData
import com.github.csandiego.timesup.data.Preset

interface PresetRepository {

    suspend fun get(presetId: Long): Preset?

    fun create(preset: Preset)

    fun getAsLiveData(presetId: Long): LiveData<Preset?>

    fun getAllByNameAscendingAsLiveData(): LiveData<List<Preset>>

    fun delete(preset: Preset)

    fun deleteAll(presets: List<Preset>)

    fun save(preset: Preset)
}