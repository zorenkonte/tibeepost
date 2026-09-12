package com.zorenkonte.tibeepost.ui

data class ColorPreset(val name: String, val hex: String)

object ColorPresets {
    val opaque = listOf(
        ColorPreset("White", "#FFFFFF"),
        ColorPreset("Light gray", "#F3F4F6"),
        ColorPreset("Dark", "#111827"),
        ColorPreset("Black", "#000000"),
        ColorPreset("Red", "#FF1744"),
        ColorPreset("Orange", "#FF6D00"),
        ColorPreset("Yellow", "#FFD600"),
        ColorPreset("Green", "#00C853"),
        ColorPreset("Blue", "#2962FF"),
        ColorPreset("Purple", "#AA00FF"),
    )

    val text = listOf(
        ColorPreset("Near black", "#111111"),
        ColorPreset("White", "#FFFFFF"),
        ColorPreset("Dark gray", "#374151"),
        ColorPreset("Red", "#FF1744"),
        ColorPreset("Yellow", "#FFD600"),
    )

    val accent = listOf(ColorPreset("None", "")) + opaque.drop(4)

    fun next(list: List<ColorPreset>, currentHex: String, step: Int): ColorPreset {
        val index = list.indexOfFirst { it.hex.equals(currentHex, ignoreCase = true) }
        val base = if (index < 0) 0 else index
        return list[((base + step) % list.size + list.size) % list.size]
    }

    fun nameOf(list: List<ColorPreset>, hex: String): String =
        list.firstOrNull { it.hex.equals(hex, ignoreCase = true) }?.name ?: hex
}
