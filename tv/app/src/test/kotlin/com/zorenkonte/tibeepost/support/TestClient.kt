package com.zorenkonte.tibeepost.support

import java.net.HttpURLConnection
import java.net.URL

data class TestResponse(val status: Int, val body: String, val headers: Map<String, List<String>>) {
    fun header(name: String): String? = headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value?.firstOrNull()
}

class TestClient(private val port: Int) {
    fun request(
        method: String,
        path: String,
        body: String? = null,
        headers: Map<String, String> = emptyMap(),
    ): TestResponse {
        val connection = URL("http://127.0.0.1:$port$path").openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = 5_000
        connection.readTimeout = 5_000
        headers.forEach { (name, value) -> connection.setRequestProperty(name, value) }
        if (body != null) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { it.write(body.toByteArray()) }
        }
        val status = connection.responseCode
        val stream = if (status >= 400) connection.errorStream else connection.inputStream
        val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
        val responseHeaders = connection.headerFields.filterKeys { it != null }
        connection.disconnect()
        return TestResponse(status, text, responseHeaders)
    }
}
