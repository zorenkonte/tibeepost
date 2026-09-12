package com.zorenkonte.tibeepost.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

object ServiceStarter {
    fun start(context: Context) {
        ContextCompat.startForegroundService(context, Intent(context, ServerService::class.java))
    }
}
