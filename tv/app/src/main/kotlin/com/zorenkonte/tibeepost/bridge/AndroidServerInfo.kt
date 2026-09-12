package com.zorenkonte.tibeepost.bridge

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.zorenkonte.tibeepost.BuildConfig
import com.zorenkonte.tibeepost.http.ServerConfig
import com.zorenkonte.tibeepost.http.ServerInfo
import com.zorenkonte.tibeepost.overlay.ScreenMetrics

class AndroidServerInfo(private val context: Context) : ServerInfo {
    override val deviceName: String
        get() = Settings.Global.getString(context.contentResolver, "device_name")?.takeIf { it.isNotBlank() }
            ?: Build.MODEL

    override val ipAddress: String?
        get() = NetworkAddress.current()

    override val port: Int = ServerConfig.PORT

    override val screenWidth: Int
        get() = ScreenMetrics.bounds(context).width()

    override val screenHeight: Int
        get() = ScreenMetrics.bounds(context).height()

    override val appVersion: String = BuildConfig.VERSION_NAME

    override val packageName: String = context.packageName

    override val overlayPermission: Boolean
        get() = Settings.canDrawOverlays(context)
}
