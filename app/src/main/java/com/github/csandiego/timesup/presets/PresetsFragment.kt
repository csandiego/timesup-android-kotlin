package com.github.csandiego.timesup.presets

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding
import kotlinx.android.synthetic.main.fragment_presets.*

class PresetsFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : Fragment(R.layout.fragment_presets) {

    constructor() : this(null)

    private val viewModel by viewModels<PresetsViewModel>(factoryProducer = viewModelFactoryProducer)
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = createRecyclerViewAdapter()
        with(recyclerView) {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            ItemTouchHelper(createItemTouchHelperCallback()).attachToRecyclerView(this)
        }
        buttonNew.setOnClickListener {
            findNavController().navigate(
                PresetsFragmentDirections.actionPresetsFragmentToNewPresetFragment()
            )
        }
        with(viewModel) {
            presets.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            selection.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    actionMode?.finish()
                } else {
                    actionMode =
                        actionMode ?: requireActivity().startActionMode(createActionModeCallback())
                    actionMode?.menu?.findItem(R.id.menuEdit)?.run {
                        isVisible = it.size == 1
                    }
                }
            }
        }
    }

    private fun startTimer(preset: Preset) {
        val s = preset.run {
            hours * 60 * 60 + minutes * 60 + seconds
        }
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, preset.name)
            putExtra(AlarmClock.EXTRA_LENGTH, s)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun createActionModeCallback() = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = when (item.itemId) {
            R.id.menuEdit -> {
                viewModel.selection.value?.let {
                    it.elementAtOrNull(0)?.let {
                        findNavController().navigate(
                            PresetsFragmentDirections
                                .actionPresetsFragmentToEditPresetFragment(it.id)
                        )
                        true
                    }
                } ?: throw IllegalStateException("No selected preset for editing")
            }
            R.id.menuDelete -> {
                viewModel.deleteSelected()
                true
            }
            else -> false
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.preset_list_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            viewModel.clearSelection()
        }
    }

    private fun createItemTouchHelperCallback() = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.START or ItemTouchHelper.END
    ) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            with(viewModel) {
                presets.value?.get(viewHolder.adapterPosition)?.let {
                    delete(it)
                } ?: throw IllegalStateException("No corresponding preset for deletion")
            }
        }
    }

    private fun createDiffUtilItemCallback() = object : DiffUtil.ItemCallback<Preset>() {

        override fun areItemsTheSame(oldItem: Preset, newItem: Preset) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Preset, newItem: Preset) = oldItem == newItem
    }

    private fun createRecyclerViewAdapter() =
        object : ListAdapter<Preset, ViewHolder>(createDiffUtilItemCallback()) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
                ListItemPresetsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).apply {
                    root.setOnLongClickListener {
                        preset?.let {
                            viewModel.run {
                                if (selection.value.isNullOrEmpty()) {
                                    toggleSelect(it)
                                    true
                                } else {
                                    false
                                }
                            }
                        } ?: false
                    }
                    root.setOnClickListener {
                        preset?.let {
                            with (viewModel) {
                                if (selection.value.isNullOrEmpty()) {
                                    startTimer(it)
                                } else {
                                    toggleSelect(it)
                                }
                            }
                        }
                    }
                    viewModel.selection.observe(viewLifecycleOwner) { selection ->
                        preset?.let {
                            root.isActivated = selection.contains(it)
                        }
                    }
                }
            )

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

    private class ViewHolder(private val binding: ListItemPresetsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: Preset) {
            with(binding) {
                this.preset = preset
                root.tag = preset.hashCode()
            }
        }
    }
}