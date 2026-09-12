package com.zorenkonte.tibeepost.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.Gravity
import android.view.WindowManager
import com.zorenkonte.tibeepost.model.Notification
import com.zorenkonte.tibeepost.model.Position

class OverlayWindowParams(private val context: Context) {

    private val overlayType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

    private val passiveFlags =
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

    fun windowContext(): Context {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return context.applicationContext
        val display = context.getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
        return context.createDisplayContext(display).createWindowContext(overlayType, null)
    }

    fun card(notification: Notification, screen: Rect): WindowManager.LayoutParams {
        val width = screen.width() * notification.widthPercent.coerceIn(10, 100) / 100
        val margin = context.resources.getDimensionPixelSize(com.zorenkonte.tibeepost.R.dimen.overlay_margin)
        return WindowManager.LayoutParams(
            width,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            passiveFlags,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = gravityFor(notification.position)
            x = if (notification.position == Position.CENTER) 0 else margin
            y = if (notification.position == Position.CENTER) 0 else margin
        }
    }

    private fun gravityFor(position: Position) = when (position) {
        Position.CENTER -> Gravity.CENTER
        Position.TOP_LEFT -> Gravity.TOP or Gravity.START
        Position.TOP_RIGHT -> Gravity.TOP or Gravity.END
        Position.BOTTOM_LEFT -> Gravity.BOTTOM or Gravity.START
        Position.BOTTOM_RIGHT -> Gravity.BOTTOM or Gravity.END
    }
}
