package com.zorenkonte.tibeepost.model

data class NotificationDefaults(
    val durationSeconds: Int = 15,
    val position: Position = Position.CENTER,
    val widthPercent: Int = 60,
    val background: Int = 0xFFFFFFFF.toInt(),
    val textColor: Int = 0xFF111111.toInt(),
    val accent: Int? = null,
    val dim: Float = 0f,
    val sound: SoundSpec = SoundSpec.Default,
)
