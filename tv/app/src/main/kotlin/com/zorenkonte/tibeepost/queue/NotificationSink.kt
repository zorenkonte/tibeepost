package com.zorenkonte.tibeepost.queue

import com.zorenkonte.tibeepost.model.Notification

interface NotificationSink {
    fun submit(notification: Notification): SubmitResult
    fun dismiss(id: String): DismissResult
}
