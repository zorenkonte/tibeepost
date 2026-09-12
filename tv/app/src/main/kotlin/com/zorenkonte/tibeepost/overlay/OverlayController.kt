package com.zorenkonte.tibeepost.overlay

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.zorenkonte.tibeepost.image.ImageFetcher
import com.zorenkonte.tibeepost.model.Notification

class OverlayController(
    private val context: Context,
    private val imageFetcher: ImageFetcher,
) {
    private val mainThread = Handler(Looper.getMainLooper())
    private val params = OverlayWindowParams(context)
    private val windowContext = params.windowContext()
    private val windowManager = windowContext.getSystemService(WindowManager::class.java)
    private var current: ShownCard? = null
    private var generation = 0

    fun canDrawOverlays(): Boolean = Settings.canDrawOverlays(context)

    fun display(notification: Notification): Boolean {
        val shown = current
        if (shown != null && shown.notification.id == notification.id) return replace(shown, notification)
        return show(notification)
    }

    fun dismissCurrent() {
        val shown = current ?: return
        current = null
        shown.dismissal?.let(mainThread::removeCallbacks)
        removeQuietly(shown.card)
        removeQuietly(shown.dim)
        shown.card.releaseBitmaps()
    }

    fun dismissAll() = dismissCurrent()

    private fun show(notification: Notification): Boolean {
        if (!canDrawOverlays()) return false
        dismissCurrent()
        val screen = ScreenMetrics.bounds(context)
        val card = NotificationCardView(windowContext)
        card.bind(notification, maxImageHeight(screen.height()))
        val cardParams = params.card(notification, screen)
        val dim = DimView(windowContext)
        dim.setOpacity(notification.dim)
        if (!addQuietly(dim, params.dim())) return false
        if (!addQuietly(card, cardParams)) {
            removeQuietly(dim)
            return false
        }

        val shown = ShownCard(notification, dim, card, cardParams, ++generation)
        current = shown
        fetchMedia(shown, screen.width(), screen.height())
        scheduleDismissal(shown)
        return true
    }

    private fun replace(shown: ShownCard, notification: Notification): Boolean {
        val screen = ScreenMetrics.bounds(context)
        shown.notification = notification
        shown.generation = ++generation
        shown.card.bind(notification, maxImageHeight(screen.height()))
        shown.cardParams = params.card(notification, screen)
        shown.dim.setOpacity(notification.dim)
        try {
            windowManager.updateViewLayout(shown.card, shown.cardParams)
        } catch (_: RuntimeException) {
            return false
        }
        fetchMedia(shown, screen.width(), screen.height())
        scheduleDismissal(shown)
        return true
    }

    private fun scheduleDismissal(shown: ShownCard) {
        shown.dismissal?.let(mainThread::removeCallbacks)
        shown.dismissal = null
        if (shown.notification.persistent) return
        val dismissal = Runnable { if (current === shown) dismissCurrent() }
        shown.dismissal = dismissal
        mainThread.postDelayed(dismissal, shown.notification.durationSeconds * 1000L)
    }

    private fun fetchMedia(shown: ShownCard, screenWidth: Int, screenHeight: Int) {
        val generationAtRequest = shown.generation
        shown.notification.imageUrl?.let { url ->
            imageFetcher.fetch(url, screenWidth / 2, maxImageHeight(screenHeight)) { bitmap ->
                if (current === shown && shown.generation == generationAtRequest) shown.card.setImage(bitmap)
            }
        }
        shown.notification.iconUrl?.let { url ->
            imageFetcher.fetch(url, ICON_MAX_PX, ICON_MAX_PX) { bitmap ->
                if (current === shown && shown.generation == generationAtRequest) shown.card.setIcon(bitmap)
            }
        }
    }

    private fun maxImageHeight(screenHeight: Int) = screenHeight * 2 / 5

    private fun addQuietly(view: View, layoutParams: WindowManager.LayoutParams): Boolean = try {
        windowManager.addView(view, layoutParams)
        true
    } catch (_: RuntimeException) {
        false
    }

    private fun removeQuietly(view: View) {
        try {
            windowManager.removeViewImmediate(view)
        } catch (_: RuntimeException) {
        }
    }

    private companion object {
        const val ICON_MAX_PX = 192
    }
}
