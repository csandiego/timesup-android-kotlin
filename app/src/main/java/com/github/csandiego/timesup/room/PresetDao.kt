package com.github.csandiego.timesup.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.csandiego.timesup.data.Preset

@Dao
interface PresetDao {

    @Query("SELECT * FROM Preset WHERE id = :presetId")
    suspend fun get(presetId: Long): Preset?

    @Insert
    suspend fun insert(preset: Preset)

    @Insert
    suspend fun insertAll(presets: List<Preset>)

    @Query("SELECT * FROM Preset WHERE id = :presetId")
    fun getAsLiveData(presetId: Long): LiveData<Preset?>

    @Query("SELECT * FROM Preset ORDER BY name ASC")
    fun getAllByNameAscendingAsLiveData(): LiveData<List<Preset>>
}