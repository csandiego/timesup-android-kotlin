package com.github.csandiego.timesup.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.csandiego.timesup.data.Preset

@Dao
interface PresetDao {

    @Query("SELECT * FROM Preset ORDER BY name ASC")
    fun getAllByNameAscendingAsLiveData(): LiveData<List<Preset>>

    @Query("SELECT * FROM Preset WHERE id = :presetId")
    suspend fun get(presetId: Long): Preset?

    @Query("DELETE FROM Preset WHERE id = :presetId")
    suspend fun delete(presetId: Long): Int

    @Query("DELETE FROM Preset WHERE id IN (:presetIds)")
    suspend fun delete(presetIds: Set<Long>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(preset: Preset)

    @Insert
    suspend fun insert(preset: Preset): Long

    @Insert
    suspend fun insert(presets: List<Preset>): List<Long>
}