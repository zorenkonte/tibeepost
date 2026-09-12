package com.zorenkonte.tibeepost.image

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ImageFetcher {
    private val executor = Executors.newFixedThreadPool(2)
    private val mainThread = Handler(Looper.getMainLooper())

    fun fetch(url: String, maxWidth: Int, maxHeight: Int, onResult: (Bitmap?) -> Unit) {
        executor.execute {
            val bitmap = try {
                download(url)?.let { BitmapDecoder.decode(it, maxWidth, maxHeight) }
            } catch (_: Exception) {
                null
            }
            mainThread.post { onResult(bitmap) }
        }
    }

    fun shutdown() = executor.shutdownNow()

    private fun download(url: String): ByteArray? {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = TIMEOUT_MS
        connection.readTimeout = TIMEOUT_MS
        connection.instanceFollowRedirects = true
        try {
            if (connection.responseCode !in 200..299) return null
            if (connection.contentLengthLong > MAX_BYTES) return null
            val buffer = ByteArrayOutputStream()
            val chunk = ByteArray(16 * 1024)
            connection.inputStream.use { input ->
                while (true) {
                    val read = input.read(chunk)
                    if (read < 0) break
                    if (buffer.size() + read > MAX_BYTES) return null
                    buffer.write(chunk, 0, read)
                }
            }
            return buffer.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val TIMEOUT_MS = 5_000
        const val MAX_BYTES = 5 * 1024 * 1024
    }
}
