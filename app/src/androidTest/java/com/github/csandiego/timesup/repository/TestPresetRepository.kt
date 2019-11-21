package com.github.csandiego.timesup.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.csandiego.timesup.data.Preset
import java.lang.Long.max

class TestPresetRepository(presets: List<Preset> = emptyList()) : PresetRepository {

    constructor(preset: Preset) : this(listOf(preset))

    private var id = 0L
    private val liveData = MutableLiveData<List<Preset>>()
    private val presets = mutableMapOf<Long, Preset>()

    init {
        saveBlocking(presets, true)
    }

    fun getBlocking(presetId: Long) = presets[presetId]?.copy()

    fun deleteBlocking(presetIds: Set<Long>, postToMainThread: Boolean = true): Int {
        val removed = mutableListOf<Preset>()
        presetIds.forEach { id ->
            presets.remove(id)?.let { preset ->
                removed.add(preset)
            }
        }
        if (removed.isNotEmpty()) {
            updateLiveData(postToMainThread)
        }
        return removed.size
    }

    fun deleteBlocking(presetId: Long, postToMainThread: Boolean = true) = deleteBlocking(setOf(presetId), postToMainThread)

    fun saveBlocking(presets: List<Preset>, postToMainThread: Boolean = true) {
        presets.forEach {
            if (it.id > 0L) {
                id = max(id, it.id)
                this@TestPresetRepository.presets[it.id] = it.copy()
            } else {
                this@TestPresetRepository.presets[++id] = it.copy(id = id)
            }
        }
        updateLiveData(postToMainThread)
    }

    fun saveBlocking(preset: Preset, postToMainThread: Boolean = true) = saveBlocking(listOf(preset), postToMainThread)

    private fun updateLiveData(postToMainThread: Boolean = true) {
        val data = presets.map { it.value.copy() }.sortedBy { it.name }
        if (postToMainThread) {
            liveData.postValue(data)
        } else {
            liveData.value = data
        }
    }

    override suspend fun get(presetId: Long) = getBlocking(presetId)

    override fun getAllByNameAscendingAsLiveData(): LiveData<List<Preset>> = liveData

    override suspend fun delete(presetId: Long) = deleteBlocking(presetId, false)

    override suspend fun delete(presetIds: Set<Long>) = deleteBlocking(presetIds, false)

    override suspend fun save(preset: Preset) = saveBlocking(preset, false)
}