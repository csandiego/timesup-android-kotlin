package com.github.csandiego.timesup.newpreset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.databinding.FragmentNewPresetBinding

class NewPresetFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : Fragment() {

    constructor() : this(null)

    val viewModel by viewModels<NewPresetViewModel>(factoryProducer = viewModelFactoryProducer)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPresetBinding.inflate(inflater, container, false).apply {
            viewModel = this@NewPresetFragment.viewModel
            buttonSave.setOnClickListener {
                this@NewPresetFragment.viewModel.create()
                it.findNavController().popBackStack(R.id.presetsFragment, false)
            }
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }
}