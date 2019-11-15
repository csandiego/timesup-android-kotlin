package com.github.csandiego.timesup.repository

import androidx.lifecycle.LiveData
import com.github.csandiego.timesup.data.Preset

interface PresetRepository {

    suspend fun get(presetId: Long): Preset?

    fun getAllByNameAscendingAsLiveData(): LiveData<List<Preset>>

    suspend fun delete(presetId: Long): Int

    suspend fun delete(presetIds: Set<Long>): Int

    suspend fun save(preset: Preset)
}