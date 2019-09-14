package com.github.csandiego.timesup.repository

import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.room.PresetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DefaultPresetRepository(
    private val dao: PresetDao,
    private val coroutineScope: CoroutineScope
) : PresetRepository {

    override suspend fun get(presetId: Long) = dao.get(presetId)

    override fun create(preset: Preset) {
        coroutineScope.launch {
            dao.insert(preset)
        }
    }

    override fun getAllByNameAscendingAsDataSourceFactory() =
        dao.getAllByNameAscendingAsDataSourceFactory()
}