package com.zorenkonte.tibeepost.model

data class Notification(
    val id: String,
    val title: String?,
    val message: String,
    val imageUrl: String?,
    val iconUrl: String?,
    val durationSeconds: Int,
    val persistent: Boolean,
    val position: Position,
    val widthPercent: Int,
    val background: Int,
    val textColor: Int,
    val accent: Int?,
    val dim: Float,
)
