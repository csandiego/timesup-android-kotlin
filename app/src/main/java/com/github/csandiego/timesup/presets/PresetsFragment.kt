package com.github.csandiego.timesup.presets

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import kotlinx.android.synthetic.main.fragment_presets.*

class PresetsFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : Fragment(R.layout.fragment_presets), ActionMode.Callback, PresetsItemCallback {

    constructor() : this(null)

    private val viewModel by viewModels<PresetsViewModel>(factoryProducer = viewModelFactoryProducer)
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PresetsAdapter(this, viewModel, viewLifecycleOwner)
        with (recyclerView) {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            ItemTouchHelper(createItemTouchHelperCallback()).attachToRecyclerView(this)
        }
        fabNew.setOnClickListener {
            findNavController().navigate(
                PresetsFragmentDirections.actionPresetsFragmentToNewPresetFragment()
            )
        }
        with (viewModel) {
            presets.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            selection.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    actionMode?.finish()
                } else {
                    actionMode = requireActionMode()
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

    override fun onPresetClick(preset: Preset) {
        val selection = viewModel.selection.value
        if (selection == null || selection.isEmpty()) {
            startTimer(preset)
        } else {
            viewModel.toggleSelect(preset)
        }
    }

    override fun onPresetLongClick(preset: Preset): Boolean {
        val selection = viewModel.selection.value
        if (selection == null || selection.isEmpty()) {
            viewModel.toggleSelect(preset)
            return true
        }
        return false
    }

    private fun requireActionMode() = actionMode ?: requireActivity().startActionMode(this)

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        viewModel.clearSelection()
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
            (viewHolder as PresetsViewHolder).binding.preset?.let {
                viewModel.delete(it)
            }
        }
    }
}