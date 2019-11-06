package com.github.csandiego.timesup.timer

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.observe
import androidx.navigation.NavDeepLinkBuilder
import com.github.csandiego.timesup.R
import dagger.android.AndroidInjection
import javax.inject.Inject

class TimerService : LifecycleService() {

    @Inject
    lateinit var timer: Timer

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val args = Bundle().apply {
            putLong("presetId", timer.preset.value!!.id)
        }
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main)
            .setDestination(R.id.timerFragment)
            .setArguments(args)
            .createPendingIntent()
        val builder = NotificationCompat.Builder(this, "LOW")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(timer.preset.value!!.name)
            .setContentText(timer.timeLeft.value!!)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(1, builder.build())
        timer.timeLeft.observe(this) {
            NotificationManagerCompat.from(this)
                .notify(1, builder.setContentText(it).build())
        }
        timer.showNotification.observe(this) {
            if (it) {
                timer.showNotificationHandled()
                NotificationManagerCompat.from(this).notify(
                    1,
                    builder.setChannelId("HIGH").setPriority(NotificationCompat.PRIORITY_HIGH).build()
                )
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class Binder : android.os.Binder() {

        val timer: Timer get() = this@TimerService.timer
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return Binder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        super.onUnbind(intent)
        return true
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        timer.timeLeft.removeObservers(this)
        timer.showNotification.removeObservers(this)
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer.state.value == Timer.State.STARTED) {
            timer.pause()
        }
    }
}