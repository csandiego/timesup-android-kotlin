package com.github.csandiego.timesup.presets

import com.github.csandiego.timesup.data.Preset
import org.junit.Before

abstract class PresetsFragmentGivenLoadedRepositoryUITest : PresetsFragmentUITest() {

    private val _presets = listOf(
        Preset(id = 1L, name = "2 seconds", seconds = 2),
        Preset(id = 2L, name = "3 seconds", seconds = 3),
        Preset(id = 3L, name = "1 second", seconds = 1)
    )
    protected val presets = _presets.sortedBy { it.name }

    @Before
    fun load() {
        repository.saveBlocking(_presets)
    }
}