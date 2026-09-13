package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.support.FakeAssets
import com.zorenkonte.tibeepost.support.FakeSink
import com.zorenkonte.tibeepost.support.TestClient
import com.zorenkonte.tibeepost.support.testRouter
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StaticAssetsTest {
    private lateinit var server: TibeeServer
    private lateinit var client: TestClient
    private val assets = FakeAssets(
        mapOf(
            "index.html" to "<!doctype html><title>TibeePost</title>",
            "assets/app.js" to "console.log('hi')",
            "assets/app.css" to "body{margin:0}",
        ),
    )

    @Before
    fun startServer() {
        server = TibeeServer(0, testRouter(FakeSink(), assets = assets, token = "tok"))
        server.start(5_000, true)
        client = TestClient(server.listeningPort)
    }

    @After
    fun stopServer() = server.stop()

    @Test
    fun rootServesIndexWithoutAuth() {
        val response = client.request("GET", "/")
        assertEquals(200, response.status)
        assertTrue(response.header("Content-Type")!!.startsWith("text/html"))
        assertTrue(response.body.contains("TibeePost"))
    }

    @Test
    fun assetsGetTheirContentType() {
        assertTrue(client.request("GET", "/assets/app.js").header("Content-Type")!!.startsWith("text/javascript"))
        assertTrue(client.request("GET", "/assets/app.css").header("Content-Type")!!.startsWith("text/css"))
    }

    @Test
    fun unknownPathWithoutExtensionFallsBackToIndex() {
        val response = client.request("GET", "/devices")
        assertEquals(200, response.status)
        assertTrue(response.body.contains("TibeePost"))
    }

    @Test
    fun missingFileIs404Json() {
        val response = client.request("GET", "/assets/missing.png")
        assertEquals(404, response.status)
        assertTrue(response.header("Content-Type")!!.startsWith("application/json"))
    }

    @Test
    fun pathTraversalIsRejected() {
        assertEquals(404, client.request("GET", "/assets/../secret").status)
    }

    @Test
    fun apiRoutesStillWin() {
        assertEquals(200, client.request("GET", "/health").status)
        assertEquals(401, client.request("GET", "/info").status)
    }

    @Test
    fun withoutBundledAssetsRootIs404() {
        val bare = TibeeServer(0, testRouter(FakeSink()))
        bare.start(5_000, true)
        try {
            assertEquals(404, TestClient(bare.listeningPort).request("GET", "/").status)
        } finally {
            bare.stop()
        }
    }
}
