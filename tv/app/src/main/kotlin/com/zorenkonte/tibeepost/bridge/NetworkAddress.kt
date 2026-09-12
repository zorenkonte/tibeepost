package com.zorenkonte.tibeepost.bridge

import java.net.Inet4Address
import java.net.NetworkInterface

object NetworkAddress {
    fun current(): String? {
        val interfaces = try {
            NetworkInterface.getNetworkInterfaces()?.toList() ?: return null
        } catch (_: Exception) {
            return null
        }
        return interfaces
            .filter { it.isUp && !it.isLoopback }
            .sortedBy { preference(it.name) }
            .flatMap { it.inetAddresses.toList() }
            .filterIsInstance<Inet4Address>()
            .firstOrNull { !it.isLoopbackAddress && !it.isLinkLocalAddress }
            ?.hostAddress
    }

    private fun preference(name: String) = when {
        name.startsWith("wlan") -> 0
        name.startsWith("eth") -> 1
        else -> 2
    }
}
