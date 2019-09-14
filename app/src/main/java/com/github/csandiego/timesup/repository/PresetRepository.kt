package com.github.csandiego.timesup.repository

import androidx.paging.DataSource
import com.github.csandiego.timesup.data.Preset

interface PresetRepository {

    suspend fun get(presetId: Long): Preset?

    fun create(preset: Preset)

    fun getAllByNameAscendingAsDataSourceFactory(): DataSource.Factory<Int, Preset>
}