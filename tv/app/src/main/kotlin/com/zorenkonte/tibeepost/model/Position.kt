package com.zorenkonte.tibeepost.model

enum class Position(val wire: String) {
    CENTER("center"),
    TOP_LEFT("top-left"),
    TOP_RIGHT("top-right"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_RIGHT("bottom-right");

    companion object {
        fun fromWire(value: String): Position? = entries.firstOrNull { it.wire == value }
    }
}
