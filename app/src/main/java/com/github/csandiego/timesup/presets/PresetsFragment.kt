package com.github.csandiego.timesup.presets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.csandiego.timesup.R
import kotlinx.android.synthetic.main.fragment_presets.*

class PresetsFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : Fragment(R.layout.fragment_presets) {

    val viewModel by viewModels<PresetsViewModel>(factoryProducer = viewModelFactoryProducer)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PresetsAdapter()
        with (recyclerView) {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.presets.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}