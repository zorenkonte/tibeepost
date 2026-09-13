package com.zorenkonte.tibeepost.http

class StaticAsset(val bytes: ByteArray, val contentType: String)

interface StaticAssets {
    fun read(path: String): StaticAsset?

    object Empty : StaticAssets {
        override fun read(path: String): StaticAsset? = null
    }
}

object ContentTypes {
    fun forPath(path: String): String = when (path.substringAfterLast('.', "").lowercase()) {
        "html" -> "text/html; charset=utf-8"
        "js", "mjs" -> "text/javascript; charset=utf-8"
        "css" -> "text/css; charset=utf-8"
        "json" -> "application/json"
        "svg" -> "image/svg+xml"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "ico" -> "image/x-icon"
        "webp" -> "image/webp"
        "woff" -> "font/woff"
        "woff2" -> "font/woff2"
        "txt" -> "text/plain; charset=utf-8"
        else -> "application/octet-stream"
    }
}
