package com.github.csandiego.timesup.presets

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
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
) : Fragment(R.layout.fragment_presets), PresetsItemCallback {

    constructor() : this(null)

    private val viewModel by viewModels<PresetsViewModel>(factoryProducer = viewModelFactoryProducer)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PresetsAdapter(this)
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
        viewModel.presets.observe(viewLifecycleOwner) {
            adapter.submitList(it)
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
        startTimer(preset)
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