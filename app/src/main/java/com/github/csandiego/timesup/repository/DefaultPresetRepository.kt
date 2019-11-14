package com.github.csandiego.timesup.repository

import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.room.PresetDao
import javax.inject.Inject

class DefaultPresetRepository @Inject constructor(private val dao: PresetDao) : PresetRepository {

    override suspend fun get(presetId: Long) = dao.get(presetId)

    override fun getAllByNameAscendingAsLiveData() = dao.getAllByNameAscendingAsLiveData()

    override suspend fun delete(presetId: Long) = dao.delete(presetId)

    override suspend fun delete(presetIds: Set<Long>) = dao.delete(presetIds)

    override suspend fun save(preset: Preset) = dao.save(preset)
}