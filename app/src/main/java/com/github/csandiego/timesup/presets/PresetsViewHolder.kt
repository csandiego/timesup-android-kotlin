package com.github.csandiego.timesup.presets

import androidx.recyclerview.widget.RecyclerView
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding

class PresetsViewHolder(private val binding: ListItemPresetsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(preset: Preset?) {
        binding.preset = preset
    }
}