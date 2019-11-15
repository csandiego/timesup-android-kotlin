package com.github.csandiego.timesup.timer

import android.content.Intent
import android.os.Bundle
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

    private var clearTimer = false

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
            .setContentText(DurationFormatter.format(timer.timeLeft.value!!))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(1, builder.build())
        timer.timeLeft.observe(this) {
            NotificationManagerCompat.from(this)
                .notify(1, builder.setContentText(DurationFormatter.format(it)).build())
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        clearTimer = true
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (clearTimer) {
            timer.clear()
        }
    }
}