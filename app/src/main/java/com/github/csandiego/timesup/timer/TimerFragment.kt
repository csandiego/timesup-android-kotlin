package com.github.csandiego.timesup.timer

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
                val navController = findNavController()
                toolbar.setupWithNavController(
                    navController,
                    AppBarConfiguration(navController.graph)
                )
            } catch (e: Exception) {
            }
        }
        with(viewModel) {
            startTimer.observe(viewLifecycleOwner) {
                if (it) {
                    startTimerHandled()
                    timer!!.start()
                }
            }
            pauseTimer.observe(viewLifecycleOwner) {
                if (it) {
                    pauseTimerHandled()
                    timer!!.pause()
                }
            }
            resetTimer.observe(viewLifecycleOwner) {
                if (it) {
                    resetTimerHandled()
                    timer!!.reset()
                }
            }
        }
        return binding.root
    }

    private var bound = false
    private var timer: Timer? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            with(timer!!) {
                state.removeObservers(viewLifecycleOwner)
                preset.removeObservers(viewLifecycleOwner)
                timeLeft.removeObservers(viewLifecycleOwner)
                showNotification.removeObservers(viewLifecycleOwner)
            }
            timer = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timer = (service as TimerService.Binder).timer.apply {
                if (state.value == Timer.State.INITIAL) {
                    load(params.presetId)
                }
                state.observe(viewLifecycleOwner) {
                    viewModel.state.value = it
                }
                preset.observe(viewLifecycleOwner) {
                    viewModel.preset.value = it
                }
                timeLeft.observe(viewLifecycleOwner) {
                    viewModel.timeLeft.value = it
                }
                showNotification.observe(viewLifecycleOwner) {
                    if (it) {
                        showNotificationHandled()
                        val context = requireContext()
                        val builder = NotificationCompat.Builder(context, "HIGH")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(preset.value!!.name)
                            .setContentText(timeLeft.value!!)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        NotificationManagerCompat.from(context).notify(1, builder.build())
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val context = requireContext()
        bound = context.bindService(
            Intent(context, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        val context = requireContext()
        timer?.run {
            if (!isRemoving &&
                setOf(
                    Timer.State.STARTED,
                    Timer.State.PAUSED,
                    Timer.State.FINISHED
                ).contains(state.value)
            ) {
                context.startService(Intent(context, TimerService::class.java))
            }
            state.removeObservers(viewLifecycleOwner)
            preset.removeObservers(viewLifecycleOwner)
            timeLeft.removeObservers(viewLifecycleOwner)
            showNotification.removeObservers(viewLifecycleOwner)
            timer = null
        }
        if (bound) {
            context.unbindService(serviceConnection)
        }
    }
}