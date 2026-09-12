package com.zorenkonte.tibeepost.support

import com.zorenkonte.tibeepost.model.Notification
import com.zorenkonte.tibeepost.queue.DismissResult
import com.zorenkonte.tibeepost.queue.NotificationSink
import com.zorenkonte.tibeepost.queue.SubmitResult

class FakeSink(
    var submitResult: SubmitResult = SubmitResult.SHOWN,
    var dismissResult: DismissResult = DismissResult.DISMISSED,
) : NotificationSink {
    val submitted = mutableListOf<Notification>()
    val dismissed = mutableListOf<String>()

    override fun submit(notification: Notification): SubmitResult {
        submitted += notification
        return submitResult
    }

    override fun dismiss(id: String): DismissResult {
        dismissed += id
        return dismissResult
    }
}
