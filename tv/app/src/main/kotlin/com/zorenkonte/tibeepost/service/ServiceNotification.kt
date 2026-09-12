package com.zorenkonte.tibeepost.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.zorenkonte.tibeepost.MainActivity
import com.zorenkonte.tibeepost.R

object ServiceNotification {
    const val ID = 1
    private const val CHANNEL_ID = "tibeepost.server"

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.channel_server),
            NotificationManager.IMPORTANCE_LOW,
        )
        channel.setShowBadge(false)
        manager.createNotificationChannel(channel)
    }

    fun build(context: Context, statusText: String): Notification {
        val openSettings = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(statusText)
            .setContentIntent(openSettings)
            .setOngoing(true)
            .build()
    }
}
