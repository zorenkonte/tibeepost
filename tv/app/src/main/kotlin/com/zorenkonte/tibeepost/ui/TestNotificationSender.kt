package com.zorenkonte.tibeepost.ui

import com.zorenkonte.tibeepost.http.ServerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TestNotificationSender {
    suspend fun send(token: String): String = withContext(Dispatchers.IO) {
        try {
            val connection = URL("http://127.0.0.1:${ServerConfig.PORT}/notify").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 3_000
            connection.readTimeout = 5_000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            if (token.isNotEmpty()) connection.setRequestProperty("Authorization", "Bearer $token")
            val payload = JSONObject()
                .put("id", "tibeepost-test")
                .put("title", "TibeePost test")
                .put("message", "If you can read this, overlays work. Sent through the local HTTP server.")
                .put("duration", 8)
            connection.outputStream.use { it.write(payload.toString().toByteArray()) }
            val status = connection.responseCode
            val stream = if (status >= 400) connection.errorStream else connection.inputStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: ""
            connection.disconnect()
            "HTTP $status $body"
        } catch (e: Exception) {
            "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }
}
