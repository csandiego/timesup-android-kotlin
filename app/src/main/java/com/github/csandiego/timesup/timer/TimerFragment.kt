package com.github.csandiego.timesup.timer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.databinding.FragmentTimerBinding
import javax.inject.Inject

class TimerFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory) : Fragment() {

    private val viewModel by viewModels<TimerViewModel> { viewModelFactory }
    private val params by navArgs<TimerFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentTimerBinding.inflate(inflater, container, false).apply {
            viewModel = this@TimerFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            try {
                with(findNavController()) {
                    toolbar.setupWithNavController(this, AppBarConfiguration(graph)
                    )
                }
            } catch (e: Exception) {
            }
        }
        with(viewModel) {
            timer.state.observe(viewLifecycleOwner) {
                if (it == Timer.State.INITIAL) {
                    timer.load(params.presetId)
                }
            }
            timer.showNotification.observe(viewLifecycleOwner) {
                if (it) {
                    timer.showNotificationHandled()
                    val args = Bundle().apply {
                        putLong("presetId", params.presetId)
                    }
                    val pendingIntent = findNavController()
                        .createDeepLink()
                        .setDestination(R.id.timerFragment)
                        .setArguments(args)
                        .createPendingIntent()
                    val context = requireContext()
                    val builder = NotificationCompat.Builder(context, "HIGH")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(timer.preset.value!!.name)
                        .setContentText(timer.timeLeft.value!!)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    NotificationManagerCompat.from(context).notify(1, builder.build())
                }
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        with(requireContext()) {
            stopService(Intent(this, TimerService::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
        with(requireActivity()) {
            if (isRemoving || isFinishing) {
                viewModel.timer.clear()
            } else if (!isRemoving && !isChangingConfigurations && !isFinishing &&
                setOf(Timer.State.STARTED, Timer.State.PAUSED, Timer.State.FINISHED).contains(
                    viewModel.timer.state.value
                )) {
                with(requireContext()) {
                    startService(Intent(this, TimerService::class.java))
                }
            }
        }
    }
}