package com.zorenkonte.tibeepost.overlay

import android.content.Context
import android.graphics.Color
import android.view.View

class DimView(context: Context) : View(context) {
    init {
        setBackgroundColor(Color.BLACK)
    }

    fun setOpacity(opacity: Float) {
        alpha = opacity.coerceIn(0f, 1f)
    }
}
