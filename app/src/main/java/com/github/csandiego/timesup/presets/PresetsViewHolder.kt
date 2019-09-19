package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding

class PresetsViewHolder(
    val binding: ListItemPresetsBinding,
    private val callback: PresetsItemCallback
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(preset: Preset) {
        with (binding) {
            this.preset = preset
            with (root) {
                tag = preset.hashCode()
                setOnClickListener {
                    callback.onPresetClick(preset)
                }
            }
        }
    }
}