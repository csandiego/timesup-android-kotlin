package com.github.csandiego.timesup.repository

import androidx.lifecycle.LiveData
import com.github.csandiego.timesup.data.Preset

interface PresetRepository {

    suspend fun get(presetId: Long): Preset?

    fun getAsLiveData(presetId: Long): LiveData<Preset?>

    fun getAllByNameAscendingAsLiveData(): LiveData<List<Preset>>

    fun delete(presetId: Long)

    fun delete(presetIds: Set<Long>)

    fun save(preset: Preset)
}