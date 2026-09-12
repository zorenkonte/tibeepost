package com.zorenkonte.tibeepost.queue

import com.zorenkonte.tibeepost.model.Notification
import com.zorenkonte.tibeepost.model.Position
import com.zorenkonte.tibeepost.model.SoundSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationQueueTest {
    private fun notification(id: String, message: String = id) = Notification(
        id = id,
        title = null,
        message = message,
        imageUrl = null,
        iconUrl = null,
        durationSeconds = 5,
        persistent = false,
        position = Position.CENTER,
        widthPercent = 60,
        background = -1,
        textColor = -16777216,
        accent = null,
        dim = 0f,
        sound = SoundSpec.None,
        speak = false,
    )

    @Test
    fun pollsInArrivalOrder() {
        val queue = NotificationQueue()
        queue.offer(notification("a"))
        queue.offer(notification("b"))
        queue.offer(notification("c"))
        assertEquals(listOf("a", "b", "c"), listOf(queue.poll()!!.id, queue.poll()!!.id, queue.poll()!!.id))
        assertNull(queue.poll())
    }

    @Test
    fun dropsOldestWhenFull() {
        val queue = NotificationQueue(capacity = 3)
        listOf("a", "b", "c").forEach { assertEquals(NotificationQueue.Offer.ADDED, queue.offer(notification(it))) }
        assertEquals(NotificationQueue.Offer.DROPPED_OLDEST, queue.offer(notification("d")))
        assertEquals(3, queue.size)
        assertEquals("b", queue.poll()!!.id)
    }

    @Test
    fun sameIdReplacesInPlaceAndKeepsPosition() {
        val queue = NotificationQueue()
        queue.offer(notification("a"))
        queue.offer(notification("b", "first"))
        queue.offer(notification("c"))
        assertEquals(NotificationQueue.Offer.REPLACED, queue.offer(notification("b", "second")))
        assertEquals(3, queue.size)
        queue.poll()
        assertEquals("second", queue.poll()!!.message)
    }

    @Test
    fun removeById() {
        val queue = NotificationQueue()
        queue.offer(notification("a"))
        queue.offer(notification("b"))
        assertTrue(queue.remove("a"))
        assertFalse(queue.remove("zzz"))
        assertEquals("b", queue.poll()!!.id)
    }

    @Test
    fun defaultCapacityIsTwenty() {
        val queue = NotificationQueue()
        repeat(25) { queue.offer(notification("n$it")) }
        assertEquals(20, queue.size)
        assertEquals("n5", queue.poll()!!.id)
    }
}
