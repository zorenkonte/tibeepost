package com.zorenkonte.tibeepost.overlay

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.zorenkonte.tibeepost.image.ImageFetcher
import com.zorenkonte.tibeepost.model.Notification
import com.zorenkonte.tibeepost.queue.DismissResult
import com.zorenkonte.tibeepost.queue.NotificationQueue
import com.zorenkonte.tibeepost.queue.SubmitResult
import com.zorenkonte.tibeepost.sound.SoundPlayer

class OverlayController(
    private val context: Context,
    private val imageFetcher: ImageFetcher,
    private val soundPlayer: SoundPlayer,
) {
    private val mainThread = Handler(Looper.getMainLooper())
    private val params = OverlayWindowParams(context)
    private val windowContext = params.windowContext()
    private val windowManager = windowContext.getSystemService(WindowManager::class.java)
    private val queue = NotificationQueue()
    private var current: ShownCard? = null
    private var sticky: Notification? = null
    private var generation = 0

    fun canDrawOverlays(): Boolean = Settings.canDrawOverlays(context)

    fun submit(notification: Notification): SubmitResult {
        if (!canDrawOverlays()) return SubmitResult.NO_OVERLAY_PERMISSION
        val shown = current

        if (shown != null && shown.notification.id == notification.id) {
            if (notification.persistent) sticky = notification else if (sticky?.id == notification.id) sticky = null
            return if (replace(shown, notification)) SubmitResult.REPLACED else SubmitResult.FAILED
        }

        if (notification.persistent) {
            val previousSticky = sticky
            sticky = notification
            queue.remove(notification.id)
            if (shown == null) return if (show(notification, playSound = true)) SubmitResult.SHOWN else SubmitResult.FAILED
            if (previousSticky != null && shown.notification.id == previousSticky.id) {
                return if (replace(shown, notification)) SubmitResult.REPLACED else SubmitResult.FAILED
            }
            return SubmitResult.QUEUED
        }

        if (shown == null) return if (show(notification, playSound = true)) SubmitResult.SHOWN else SubmitResult.FAILED
        if (shown.notification.id == sticky?.id) {
            return if (show(notification, playSound = true)) SubmitResult.SHOWN else SubmitResult.FAILED
        }
        return when (queue.offer(notification)) {
            NotificationQueue.Offer.DROPPED_OLDEST -> SubmitResult.QUEUED_DROPPED_OLDEST
            else -> SubmitResult.QUEUED
        }
    }

    fun dismiss(id: String): DismissResult {
        var result = DismissResult.UNKNOWN
        if (sticky?.id == id) {
            sticky = null
            result = DismissResult.DISMISSED
        }
        if (queue.remove(id)) result = DismissResult.REMOVED_FROM_QUEUE
        val shown = current
        if (shown != null && shown.notification.id == id) {
            tearDown(shown)
            pump()
            return DismissResult.DISMISSED
        }
        return result
    }

    fun dismissCurrent() {
        current?.let(::tearDown)
        pump()
    }

    fun dismissAll() {
        queue.clear()
        sticky = null
        current?.let(::tearDown)
    }

    private fun pump() {
        if (current != null) return
        queue.poll()?.let {
            show(it, playSound = true)
            return
        }
        sticky?.let { show(it, playSound = false) }
    }

    private fun show(notification: Notification, playSound: Boolean): Boolean {
        current?.let(::tearDown)
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
        if (playSound) soundPlayer.play(notification)
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
        soundPlayer.play(notification)
        return true
    }

    private fun tearDown(shown: ShownCard) {
        if (current === shown) current = null
        shown.dismissal?.let(mainThread::removeCallbacks)
        removeQuietly(shown.card)
        removeQuietly(shown.dim)
        shown.card.releaseBitmaps()
    }

    private fun scheduleDismissal(shown: ShownCard) {
        shown.dismissal?.let(mainThread::removeCallbacks)
        shown.dismissal = null
        if (shown.notification.persistent) return
        val dismissal = Runnable {
            if (current !== shown) return@Runnable
            tearDown(shown)
            pump()
        }
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
