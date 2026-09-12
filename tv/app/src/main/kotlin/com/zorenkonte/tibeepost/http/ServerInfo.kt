package com.zorenkonte.tibeepost.http

interface ServerInfo {
    val deviceName: String
    val ipAddress: String?
    val port: Int
    val screenWidth: Int
    val screenHeight: Int
    val appVersion: String
    val overlayPermission: Boolean
}
