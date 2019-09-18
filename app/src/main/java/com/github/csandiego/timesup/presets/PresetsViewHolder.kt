package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding

class PresetsViewHolder(
    private val binding: ListItemPresetsBinding,
    private val callback: PresetsItemCallback
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(preset: Preset) {
        binding.preset = preset
        binding.root.tag = "list_item_preset_${preset.id}"
        binding.root.setOnClickListener {
            callback.onPresetClick(preset)
        }
    }
}