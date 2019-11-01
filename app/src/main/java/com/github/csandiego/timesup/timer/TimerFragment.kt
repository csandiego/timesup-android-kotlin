package com.github.csandiego.timesup.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.github.csandiego.timesup.databinding.FragmentTimerBinding
import javax.inject.Inject

class TimerFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory): Fragment() {

    private val viewModel by viewModels<TimerViewModel> { viewModelFactory }
    private val params by navArgs<TimerFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load(params.presetId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTimerBinding.inflate(inflater, container, false).apply {
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            toolbar.setupWithNavController(navController, appBarConfiguration)
            viewModel = this@TimerFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }
}