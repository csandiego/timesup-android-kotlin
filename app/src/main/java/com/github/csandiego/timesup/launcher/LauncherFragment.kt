package com.github.csandiego.timesup.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.csandiego.timesup.databinding.FragmentLauncherBinding

class LauncherFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : Fragment() {

    constructor() : this(null)

    val viewModel by viewModels<LauncherViewModel>(factoryProducer = viewModelFactoryProducer)
    private val params by navArgs<LauncherFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load(params.presetId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLauncherBinding.inflate(inflater, container, false).apply {
            viewModel = this@LauncherFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            toolbar.setNavigationOnClickListener {
                this@LauncherFragment.findNavController().navigateUp()
            }
        }
        return binding.root
    }
}