package com.zorenkonte.tibeepost.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.image.ImageFetcher
import com.zorenkonte.tibeepost.overlay.OverlayController

class ServerService : Service() {

    private lateinit var imageFetcher: ImageFetcher
    lateinit var overlay: OverlayController
        private set

    override fun onCreate() {
        super.onCreate()
        imageFetcher = ImageFetcher()
        overlay = OverlayController(this, imageFetcher)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        promoteToForeground(getString(R.string.status_starting))
        return START_STICKY
    }

    override fun onDestroy() {
        overlay.dismissAll()
        imageFetcher.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun promoteToForeground(statusText: String) {
        val notification = ServiceNotification.build(this, statusText)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(ServiceNotification.ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(ServiceNotification.ID, notification)
        }
    }
}
