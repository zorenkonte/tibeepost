package com.zorenkonte.tibeepost.queue

enum class SubmitResult(val wire: String) {
    SHOWN("shown"),
    REPLACED("replaced"),
    QUEUED("queued"),
    QUEUED_DROPPED_OLDEST("queued-dropped-oldest"),
    NO_OVERLAY_PERMISSION("no-overlay-permission"),
    FAILED("failed"),
}

enum class DismissResult(val wire: String) {
    DISMISSED("dismissed"),
    REMOVED_FROM_QUEUE("removed-from-queue"),
    UNKNOWN("unknown"),
}
