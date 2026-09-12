package com.zorenkonte.tibeepost.model

sealed class SoundSpec {
    data object None : SoundSpec()
    data object Default : SoundSpec()
    data class Url(val url: String) : SoundSpec()

    fun toWire(): String = when (this) {
        None -> "none"
        Default -> "default"
        is Url -> url
    }

    companion object {
        fun fromWire(value: String): SoundSpec? = when {
            value == "none" -> None
            value == "default" -> Default
            value.startsWith("http://") || value.startsWith("https://") -> Url(value)
            else -> null
        }
    }
}
