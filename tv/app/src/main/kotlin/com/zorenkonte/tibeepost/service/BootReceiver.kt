package com.zorenkonte.tibeepost.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zorenkonte.tibeepost.settings.Settings

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (Settings(context).autostart) ServiceStarter.start(context)
    }
}
