package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.support.FakeSink
import com.zorenkonte.tibeepost.support.TestClient
import com.zorenkonte.tibeepost.support.TestResponse
import com.zorenkonte.tibeepost.support.testRouter
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CorsTest {
    private lateinit var server: TibeeServer
    private lateinit var client: TestClient

    @Before
    fun startServer() {
        server = TibeeServer(0, testRouter(FakeSink()))
        server.start(5_000, true)
        client = TestClient(server.listeningPort)
    }

    @After
    fun stopServer() = server.stop()

    private fun assertCors(response: TestResponse) {
        assertEquals("*", response.header("Access-Control-Allow-Origin"))
        assertEquals("GET, POST, DELETE, OPTIONS", response.header("Access-Control-Allow-Methods"))
        assertEquals("Content-Type, Authorization", response.header("Access-Control-Allow-Headers"))
        assertEquals("86400", response.header("Access-Control-Max-Age"))
    }

    @Test
    fun preflightOnNotifyIs204WithHeaders() {
        val response = client.request(
            "OPTIONS",
            "/notify",
            headers = mapOf(
                "Origin" to "http://localhost:5173",
                "Access-Control-Request-Method" to "POST",
                "Access-Control-Request-Headers" to "content-type, authorization",
            ),
        )
        assertEquals(204, response.status)
        assertCors(response)
    }

    @Test
    fun preflightOnDeletePathIs204() {
        assertEquals(204, client.request("OPTIONS", "/notify/some-id").status)
    }

    @Test
    fun successResponsesCarryCors() {
        assertCors(client.request("GET", "/health"))
        assertCors(client.request("POST", "/notify", """{"message":"m"}"""))
    }

    @Test
    fun errorResponsesCarryCors() {
        assertCors(client.request("GET", "/missing"))
        assertCors(client.request("POST", "/notify", "{bad"))
    }
}
