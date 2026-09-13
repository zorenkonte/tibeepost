package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.queue.DismissResult
import com.zorenkonte.tibeepost.queue.SubmitResult
import com.zorenkonte.tibeepost.support.FakeServerInfo
import com.zorenkonte.tibeepost.support.FakeSink
import com.zorenkonte.tibeepost.support.testRouter
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouterTest {
    private fun get(router: Router, path: String) = router.handle(HttpRequest("GET", path, emptyMap(), ""))
    private fun post(router: Router, path: String, body: String) = router.handle(HttpRequest("POST", path, emptyMap(), body))
    private fun delete(router: Router, path: String) = router.handle(HttpRequest("DELETE", path, emptyMap(), ""))

    @Test
    fun trailingSlashIsIgnored() {
        assertEquals(200, get(testRouter(), "/health/").status)
    }

    @Test
    fun infoReportsNullIpWhenOffline() {
        val body = JSONObject(get(testRouter(info = FakeServerInfo(ipAddress = null)), "/info").text)
        assertEquals(JSONObject.NULL, body.get("ip"))
    }

    @Test
    fun exceptionsBecome500Json() {
        val broken = testRouter(info = object : ServerInfo by FakeServerInfo() {
            override val deviceName: String get() = throw IllegalStateException("boom")
        })
        val result = get(broken, "/info")
        assertEquals(500, result.status)
        assertEquals("boom", JSONObject(result.text).getString("error"))
    }

    @Test
    fun validNotifyReachesSink() {
        val sink = FakeSink()
        val result = post(testRouter(sink), "/notify", """{"id":"a","message":"hello"}""")
        assertEquals(200, result.status)
        assertEquals("a", sink.submitted.single().id)
        val body = JSONObject(result.text)
        assertEquals("a", body.getString("id"))
        assertEquals("shown", body.getString("result"))
    }

    @Test
    fun replacedAndQueuedAreReported() {
        val sink = FakeSink(submitResult = SubmitResult.REPLACED)
        assertEquals("replaced", JSONObject(post(testRouter(sink), "/notify", """{"message":"m"}""").text).getString("result"))
        sink.submitResult = SubmitResult.QUEUED
        assertEquals("queued", JSONObject(post(testRouter(sink), "/notify", """{"message":"m"}""").text).getString("result"))
    }

    @Test
    fun invalidNotifyIs400NamingField() {
        val sink = FakeSink()
        val result = post(testRouter(sink), "/notify", """{"message":"m","duration":"soon"}""")
        assertEquals(400, result.status)
        assertTrue(JSONObject(result.text).getString("error").contains("'duration'"))
        assertTrue(sink.submitted.isEmpty())
    }

    @Test
    fun missingOverlayPermissionIs503WithAdbHint() {
        val sink = FakeSink(submitResult = SubmitResult.NO_OVERLAY_PERMISSION)
        val result = post(testRouter(sink), "/notify", """{"message":"m"}""")
        assertEquals(503, result.status)
        assertTrue(JSONObject(result.text).getString("error").contains("appops set com.zorenkonte.tibeepost"))
    }

    @Test
    fun deleteDismissesById() {
        val sink = FakeSink()
        val result = delete(testRouter(sink), "/notify/order-1")
        assertEquals(200, result.status)
        assertEquals(listOf("order-1"), sink.dismissed)
        assertEquals("dismissed", JSONObject(result.text).getString("result"))
    }

    @Test
    fun deleteUnknownIs404() {
        val sink = FakeSink(dismissResult = DismissResult.UNKNOWN)
        assertEquals(404, delete(testRouter(sink), "/notify/nope").status)
    }

    @Test
    fun getOnNotifyIs405() {
        assertEquals(405, get(testRouter(), "/notify").status)
    }
}
