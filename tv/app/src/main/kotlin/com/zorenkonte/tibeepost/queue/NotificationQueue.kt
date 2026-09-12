package com.zorenkonte.tibeepost.queue

import com.zorenkonte.tibeepost.model.Notification

class NotificationQueue(private val capacity: Int = DEFAULT_CAPACITY) {
    enum class Offer { ADDED, REPLACED, DROPPED_OLDEST }

    private val items = ArrayDeque<Notification>()

    val size: Int get() = items.size

    fun offer(notification: Notification): Offer {
        val existing = items.indexOfFirst { it.id == notification.id }
        if (existing >= 0) {
            items[existing] = notification
            return Offer.REPLACED
        }
        var dropped = false
        while (items.size >= capacity) {
            items.removeFirst()
            dropped = true
        }
        items.addLast(notification)
        return if (dropped) Offer.DROPPED_OLDEST else Offer.ADDED
    }

    fun poll(): Notification? = items.removeFirstOrNull()

    fun remove(id: String): Boolean = items.removeAll { it.id == id }

    fun clear() = items.clear()

    companion object {
        const val DEFAULT_CAPACITY = 20
    }
}
