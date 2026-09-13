package com.zorenkonte.tibeepost.ui

data class TibeeUiState(
    val ipAddress: String?,
    val port: Int,
    val packageName: String,
    val serverReachable: Boolean?,
    val overlayPermitted: Boolean,
    val token: String,
    val autostart: Boolean,
    val widthPercent: Int,
    val durationSeconds: Int,
    val dim: Float,
    val background: String,
    val textColor: String,
    val accent: String,
    val position: String,
    val sound: String,
    val testResult: String,
    val lastOpenedSettings: String?,
) {
    val address: String get() = "http://${ipAddress ?: "no-network"}:$port"
    val authEnabled: Boolean get() = token.isNotEmpty()
}
