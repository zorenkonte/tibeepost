package com.zorenkonte.tibeepost.bridge

import android.os.Handler
import android.os.Looper
import com.zorenkonte.tibeepost.model.Notification
import com.zorenkonte.tibeepost.overlay.OverlayController
import com.zorenkonte.tibeepost.queue.DismissResult
import com.zorenkonte.tibeepost.queue.NotificationSink
import com.zorenkonte.tibeepost.queue.SubmitResult
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainThreadSink(private val controller: OverlayController) : NotificationSink {
    private val mainThread = Handler(Looper.getMainLooper())

    override fun submit(notification: Notification): SubmitResult =
        onMainThread { controller.submit(notification) } ?: SubmitResult.QUEUED

    override fun dismiss(id: String): DismissResult =
        onMainThread { controller.dismiss(id) } ?: DismissResult.UNKNOWN

    private fun <T> onMainThread(block: () -> T): T? {
        if (Looper.myLooper() == Looper.getMainLooper()) return block()
        val task = FutureTask(block)
        mainThread.post(task)
        return try {
            task.get(REPLY_TIMEOUT_S, TimeUnit.SECONDS)
        } catch (_: TimeoutException) {
            null
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    private companion object {
        const val REPLY_TIMEOUT_S = 2L
    }
}
