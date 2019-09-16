package com.github.csandiego.timesup.repository

import android.content.Context
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.room.PresetDao
import com.github.csandiego.timesup.room.TimesUpDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DefaultPresetRepository(
    private val dao: PresetDao,
    private val coroutineScope: CoroutineScope
) : PresetRepository {

    companion object {
        private var instance: DefaultPresetRepository? = null

        fun getInstance(context: Context): DefaultPresetRepository {
            val currentInstance = instance
            if (currentInstance != null)
                return currentInstance

            return synchronized(this) {
                DefaultPresetRepository(
                    TimesUpDatabase.getInstance(context).presetDao(),
                    CoroutineScope(SupervisorJob() + Dispatchers.IO)
                ).also {
                    instance = it
                }
            }
        }
    }

    override suspend fun get(presetId: Long) = dao.get(presetId)

    override fun create(preset: Preset) {
        coroutineScope.launch {
            dao.insert(preset)
        }
    }

    override fun getAllByNameAscendingAsDataSourceFactory() =
        dao.getAllByNameAscendingAsDataSourceFactory()

    override fun getAsLiveData(presetId: Long) = dao.getAsLiveData(presetId)
}