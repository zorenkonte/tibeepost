package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.support.FakeSink
import com.zorenkonte.tibeepost.support.TestClient
import com.zorenkonte.tibeepost.support.testRouter
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BearerAuthTest {
    private lateinit var server: TibeeServer
    private lateinit var client: TestClient
    private val sink = FakeSink()

    @Before
    fun startServer() {
        server = TibeeServer(0, testRouter(sink, token = "secret-token"))
        server.start(5_000, true)
        client = TestClient(server.listeningPort)
    }

    @After
    fun stopServer() = server.stop()

    @Test
    fun emptyTokenDisablesAuth() {
        assertTrue(BearerAuth.isAuthorized(HttpRequest("POST", "/notify", emptyMap(), ""), ""))
    }

    @Test
    fun matchingBearerIsAccepted() {
        val request = HttpRequest("POST", "/notify", mapOf("authorization" to "Bearer abc"), "")
        assertTrue(BearerAuth.isAuthorized(request, "abc"))
    }

    @Test
    fun wrongOrMalformedBearerIsRejected() {
        assertFalse(BearerAuth.isAuthorized(HttpRequest("POST", "/notify", mapOf("authorization" to "Bearer nope"), ""), "abc"))
        assertFalse(BearerAuth.isAuthorized(HttpRequest("POST", "/notify", mapOf("authorization" to "abc"), ""), "abc"))
        assertFalse(BearerAuth.isAuthorized(HttpRequest("POST", "/notify", emptyMap(), ""), "abc"))
    }

    @Test
    fun healthStaysOpen() {
        assertEquals(200, client.request("GET", "/health").status)
    }

    @Test
    fun preflightStaysOpen() {
        assertEquals(204, client.request("OPTIONS", "/notify").status)
    }

    @Test
    fun notifyWithoutTokenIs401WithChallenge() {
        val response = client.request("POST", "/notify", """{"message":"m"}""")
        assertEquals(401, response.status)
        assertEquals("Bearer", response.header("WWW-Authenticate"))
        assertEquals("*", response.header("Access-Control-Allow-Origin"))
        assertTrue(sink.submitted.isEmpty())
    }

    @Test
    fun notifyWithWrongTokenIs401() {
        val response = client.request("POST", "/notify", """{"message":"m"}""", mapOf("Authorization" to "Bearer wrong"))
        assertEquals(401, response.status)
    }

    @Test
    fun notifyWithTokenSucceeds() {
        val response = client.request("POST", "/notify", """{"message":"m"}""", mapOf("Authorization" to "Bearer secret-token"))
        assertEquals(200, response.status)
        assertEquals(1, sink.submitted.size)
    }

    @Test
    fun infoReportsAuthEnabledAndRequiresToken() {
        assertEquals(401, client.request("GET", "/info").status)
        val body = JSONObject(client.request("GET", "/info", headers = mapOf("Authorization" to "Bearer secret-token")).body)
        assertTrue(body.getBoolean("authEnabled"))
    }
}
