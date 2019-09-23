package com.github.csandiego.timesup.repository

import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.room.PresetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultPresetRepository @Inject constructor(
    private val dao: PresetDao,
    private val coroutineScope: CoroutineScope
) : PresetRepository {

    override suspend fun get(presetId: Long) = dao.get(presetId)

    override fun create(preset: Preset) {
        coroutineScope.launch {
            dao.insert(preset)
        }
    }

    override fun getAsLiveData(presetId: Long) = dao.getAsLiveData(presetId)

    override fun getAllByNameAscendingAsLiveData() = dao.getAllByNameAscendingAsLiveData()

    override fun delete(preset: Preset) {
        coroutineScope.launch {
            dao.delete(preset)
        }
    }

    override fun deleteAll(presets: List<Preset>) {
        coroutineScope.launch {
            dao.deleteAll(presets)
        }
    }

    override fun save(preset: Preset) {
        coroutineScope.launch {
            dao.save(preset)
        }
    }
}