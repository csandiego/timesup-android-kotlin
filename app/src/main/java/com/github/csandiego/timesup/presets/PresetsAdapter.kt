package com.github.csandiego.timesup.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding

class PresetsAdapter(
    private val callback: PresetsItemCallback,
    private val viewModel: PresetsViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<Preset, PresetsViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PresetsViewHolder(
        ListItemPresetsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        callback,
        viewModel,
        lifecycleOwner
    )

    override fun onBindViewHolder(holder: PresetsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemCallback : DiffUtil.ItemCallback<Preset>() {
        override fun areItemsTheSame(oldItem: Preset, newItem: Preset) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Preset, newItem: Preset) = oldItem == newItem
    }
}