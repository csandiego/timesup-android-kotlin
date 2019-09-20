package com.github.csandiego.timesup.presets

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding

class PresetsViewHolder(
    val binding: ListItemPresetsBinding,
    callback: PresetsItemCallback,
    viewModel: PresetsViewModel,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {

    init {
        with (binding) {
            root.setOnLongClickListener {
                preset?.let {
                    callback.onPresetLongClick(it)
                } ?: false
            }
            root.setOnClickListener {
                preset?.let {
                    callback.onPresetClick(it)
                }
            }
            viewModel.selection.observe(lifecycleOwner) { selection ->
                preset?.let {
                    root.isActivated = selection.contains(it)
                }
            }
        }
    }

    fun bind(preset: Preset) {
        with (binding) {
            this.preset = preset
            root.tag = preset.hashCode()
        }
    }
}