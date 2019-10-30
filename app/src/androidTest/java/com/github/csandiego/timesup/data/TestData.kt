package com.github.csandiego.timesup.data

object TestData {

    val presets = mutableListOf<Preset>().apply {
        for (i in 1..9) {
            add(Preset(id = i.toLong(), name = String.format("%02d minutes", i), minutes = i))
        }
        add(Preset(id = size + 1L, name = "02 seconds", seconds = 2))
    }

    val presetsSortedByName = presets.sortedBy { it.name }

    val emptyPreset = Preset()

    val newPreset = Preset(id = presets.size + 1L, name = "01 hours", hours = 1)

    val editPreset = presetsSortedByName[0]

    val updatedPreset = Preset(id = editPreset.id, name = "01 hours", hours = 1)
}