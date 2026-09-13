package com.zorenkonte.tibeepost.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.bridge.AndroidServerInfo
import com.zorenkonte.tibeepost.bridge.AssetStaticAssets
import com.zorenkonte.tibeepost.bridge.MainThreadSink
import com.zorenkonte.tibeepost.bridge.NetworkAddress
import com.zorenkonte.tibeepost.http.Router
import com.zorenkonte.tibeepost.http.ServerConfig
import com.zorenkonte.tibeepost.http.TibeeServer
import com.zorenkonte.tibeepost.image.ImageFetcher
import com.zorenkonte.tibeepost.model.NotificationPayloadParser
import com.zorenkonte.tibeepost.overlay.OverlayController
import com.zorenkonte.tibeepost.settings.Settings
import com.zorenkonte.tibeepost.sound.SoundPlayer
import java.io.IOException

class ServerService : Service() {

    private val mainThread = Handler(Looper.getMainLooper())
    private lateinit var imageFetcher: ImageFetcher
    private lateinit var soundPlayer: SoundPlayer
    private lateinit var server: TibeeServer
    private var restartAttempts = 0
    lateinit var overlay: OverlayController
        private set

    override fun onCreate() {
        super.onCreate()
        imageFetcher = ImageFetcher()
        soundPlayer = SoundPlayer(this)
        overlay = OverlayController(this, imageFetcher, soundPlayer)
        val settings = Settings(this)
        val router = Router(
            info = AndroidServerInfo(this),
            sink = MainThreadSink(overlay),
            parser = NotificationPayloadParser(),
            defaults = settings::toDefaults,
            token = settings::token,
            assets = AssetStaticAssets(assets, "web"),
        )
        server = TibeeServer(ServerConfig.PORT, router)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        promoteToForeground(getString(R.string.status_starting))
        startServer()
        return START_STICKY
    }

    override fun onDestroy() {
        mainThread.removeCallbacksAndMessages(null)
        server.stop()
        overlay.dismissAll()
        soundPlayer.release()
        imageFetcher.shutdown()
        super.onDestroy()
    }

    private fun startServer() {
        if (server.isAlive) return
        try {
            server.start(ServerConfig.SOCKET_READ_TIMEOUT_MS, false)
            restartAttempts = 0
            promoteToForeground(getString(R.string.status_listening, NetworkAddress.current() ?: "?", ServerConfig.PORT))
        } catch (_: IOException) {
            val delay = (1_000L shl restartAttempts.coerceAtMost(5))
            restartAttempts++
            promoteToForeground(getString(R.string.status_port_busy, ServerConfig.PORT))
            mainThread.postDelayed(::startServer, delay)
        }
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
