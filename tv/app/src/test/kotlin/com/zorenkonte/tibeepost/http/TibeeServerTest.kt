package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.support.FakeSink
import com.zorenkonte.tibeepost.support.TestClient
import com.zorenkonte.tibeepost.support.testRouter
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TibeeServerTest {
    private lateinit var server: TibeeServer
    private lateinit var client: TestClient
    private val sink = FakeSink()

    @Before
    fun startServer() {
        server = TibeeServer(0, testRouter(sink))
        server.start(5_000, true)
        client = TestClient(server.listeningPort)
    }

    @After
    fun stopServer() = server.stop()

    @Test
    fun healthReportsVersion() {
        val response = client.request("GET", "/health")
        assertEquals(200, response.status)
        val json = JSONObject(response.body)
        assertEquals("ok", json.getString("status"))
        assertEquals("0.1.0-test", json.getString("version"))
        assertTrue(response.header("Content-Type")!!.startsWith("application/json"))
    }

    @Test
    fun infoReportsDeviceDetails() {
        val json = JSONObject(client.request("GET", "/info").body)
        assertEquals("Test TV", json.getString("deviceName"))
        assertEquals("192.168.1.50", json.getString("ip"))
        assertEquals(8090, json.getInt("port"))
        assertEquals(1920, json.getInt("screenWidth"))
        assertEquals(1080, json.getInt("screenHeight"))
        assertTrue(json.getBoolean("overlayPermission"))
    }

    @Test
    fun unknownRouteIsJson404() {
        val response = client.request("GET", "/nothing")
        assertEquals(404, response.status)
        assertTrue(JSONObject(response.body).getString("error").contains("/nothing"))
    }

    @Test
    fun wrongMethodIs405() {
        assertEquals(405, client.request("POST", "/health", "{}").status)
    }

    @Test
    fun postNotifyRoundTrips() {
        val response = client.request("POST", "/notify", """{"id":"n1","title":"Hi","message":"Body text"}""")
        assertEquals(200, response.status)
        assertEquals("n1", JSONObject(response.body).getString("id"))
        assertEquals("Body text", sink.submitted.single().message)
    }

    @Test
    fun postWithUtf8BodyKeepsCharacters() {
        client.request("POST", "/notify", """{"message":"Héllo wörld ✓"}""")
        assertEquals("Héllo wörld ✓", sink.submitted.single().message)
    }

    @Test
    fun deleteNotifyRoundTrips() {
        val response = client.request("DELETE", "/notify/n1")
        assertEquals(200, response.status)
        assertEquals(listOf("n1"), sink.dismissed)
    }

    @Test
    fun oversizedBodyIs413() {
        val huge = "x".repeat(ServerConfig.MAX_BODY_BYTES + 1)
        assertEquals(413, client.request("POST", "/health", huge).status)
    }
}
