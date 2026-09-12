package com.zorenkonte.tibeepost.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zorenkonte.tibeepost.model.Notification

class NotificationCardView(context: Context) : LinearLayout(context) {

    private val accentStripe = View(context)
    private val icon = ImageView(context)
    private val title = TextView(context)
    private val message = TextView(context)
    private val image = ImageView(context)
    private val background = GradientDrawable()

    init {
        orientation = HORIZONTAL
        clipToOutline = true
        background.cornerRadius = dp(16f)
        setBackground(background)
        elevation = dp(12f)

        addView(accentStripe, LayoutParams(dp(10f).toInt(), LayoutParams.MATCH_PARENT))

        val content = LinearLayout(context).apply {
            orientation = VERTICAL
            val pad = dp(28f).toInt()
            setPadding(pad, pad, pad, pad)
        }

        val header = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        icon.scaleType = ImageView.ScaleType.FIT_CENTER
        header.addView(icon, LayoutParams(dp(48f).toInt(), dp(48f).toInt()).apply { marginEnd = dp(16f).toInt() })
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
        title.typeface = Typeface.DEFAULT_BOLD
        title.maxLines = 2
        header.addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
        content.addView(header, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        message.setLineSpacing(0f, 1.15f)
        message.maxLines = 8
        content.addView(
            message,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12f).toInt() },
        )

        image.adjustViewBounds = true
        image.scaleType = ImageView.ScaleType.FIT_CENTER
        content.addView(
            image,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { topMargin = dp(20f).toInt() },
        )

        addView(content, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
    }

    fun bind(notification: Notification, maxImageHeightPx: Int) {
        background.setColor(notification.background)
        title.setTextColor(notification.textColor)
        message.setTextColor(notification.textColor)

        val accent = notification.accent
        accentStripe.visibility = if (accent == null) GONE else VISIBLE
        if (accent != null) accentStripe.setBackgroundColor(accent)

        title.text = notification.title
        title.visibility = if (notification.title.isNullOrBlank()) GONE else VISIBLE
        message.text = notification.message

        icon.visibility = if (notification.iconUrl == null) GONE else INVISIBLE
        icon.setImageDrawable(null)
        image.visibility = GONE
        image.setImageDrawable(null)
        image.maxHeight = maxImageHeightPx
    }

    fun setIcon(bitmap: Bitmap?) {
        if (bitmap == null) {
            icon.visibility = GONE
            return
        }
        icon.setImageBitmap(bitmap)
        icon.visibility = VISIBLE
    }

    fun setImage(bitmap: Bitmap?) {
        if (bitmap == null) {
            image.visibility = GONE
            return
        }
        image.setImageBitmap(bitmap)
        image.visibility = VISIBLE
    }

    fun releaseBitmaps() {
        icon.setImageDrawable(null)
        image.setImageDrawable(null)
    }

    private fun dp(value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
}
