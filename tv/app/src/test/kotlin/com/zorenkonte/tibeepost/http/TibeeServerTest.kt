package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.support.FakeServerInfo
import com.zorenkonte.tibeepost.support.TestClient
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TibeeServerTest {
    private lateinit var server: TibeeServer
    private lateinit var client: TestClient

    @Before
    fun startServer() {
        server = TibeeServer(0, Router(FakeServerInfo()))
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
    fun oversizedBodyIs413() {
        val huge = "x".repeat(ServerConfig.MAX_BODY_BYTES + 1)
        assertEquals(413, client.request("POST", "/health", huge).status)
    }
}
