package com.zorenkonte.tibeepost.model

object HexColor {
    fun parse(text: String): Int? {
        val digits = text.trim().removePrefix("#")
        if (digits.isEmpty() || !digits.all { it.isHexDigit() }) return null
        val argb = when (digits.length) {
            3 -> "FF" + digits.doubleEach()
            4 -> digits.doubleEach()
            6 -> "FF$digits"
            8 -> digits
            else -> return null
        }
        return argb.toLong(16).toInt()
    }

    fun format(argb: Int): String {
        val alpha = argb ushr 24
        return if (alpha == 0xFF) "#%06X".format(argb and 0xFFFFFF) else "#%08X".format(argb)
    }

    fun alphaOf(argb: Int): Int = argb ushr 24

    private fun Char.isHexDigit() = this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'

    private fun String.doubleEach() = buildString { this@doubleEach.forEach { append(it).append(it) } }
}
