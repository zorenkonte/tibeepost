package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.support.FakeServerInfo
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class RouterTest {
    private val router = Router(FakeServerInfo())

    @Test
    fun trailingSlashIsIgnored() {
        assertEquals(200, router.handle(HttpRequest("GET", "/health/", emptyMap(), "")).status)
    }

    @Test
    fun infoReportsNullIpWhenOffline() {
        val offline = Router(FakeServerInfo(ipAddress = null))
        val body = JSONObject(offline.handle(HttpRequest("GET", "/info", emptyMap(), "")).body)
        assertEquals(JSONObject.NULL, body.get("ip"))
    }

    @Test
    fun exceptionsBecome500Json() {
        val broken = Router(object : ServerInfo by FakeServerInfo() {
            override val deviceName: String get() = throw IllegalStateException("boom")
        })
        val result = broken.handle(HttpRequest("GET", "/info", emptyMap(), ""))
        assertEquals(500, result.status)
        assertEquals("boom", JSONObject(result.body).getString("error"))
    }
}
