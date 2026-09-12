package com.zorenkonte.tibeepost.support

import com.zorenkonte.tibeepost.http.ServerInfo

class FakeServerInfo(
    override val deviceName: String = "Test TV",
    override val ipAddress: String? = "192.168.1.50",
    override val port: Int = 8090,
    override val screenWidth: Int = 1920,
    override val screenHeight: Int = 1080,
    override val appVersion: String = "0.1.0-test",
    override val packageName: String = "com.zorenkonte.tibeepost",
    override val overlayPermission: Boolean = true,
) : ServerInfo
