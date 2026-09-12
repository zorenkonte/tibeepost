package com.zorenkonte.tibeepost.overlay

import android.view.WindowManager
import com.zorenkonte.tibeepost.model.Notification

class ShownCard(
    var notification: Notification,
    val dim: DimView,
    val card: NotificationCardView,
    var cardParams: WindowManager.LayoutParams,
    var generation: Int,
) {
    var dismissal: Runnable? = null
}
