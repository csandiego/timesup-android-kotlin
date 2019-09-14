package com.github.csandiego.timesup.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding

class PresetsAdapter : PagedListAdapter<Preset, PresetsViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PresetsViewHolder(
        ListItemPresetsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PresetsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemCallback : DiffUtil.ItemCallback<Preset>() {
        override fun areItemsTheSame(oldItem: Preset, newItem: Preset) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Preset, newItem: Preset) = oldItem == newItem
    }
}