package com.zorenkonte.tibeepost.http

class HttpResult(
    val status: Int,
    val body: ByteArray,
    val contentType: String = "application/json",
    val headers: Map<String, String> = emptyMap(),
) {
    constructor(status: Int, body: String, headers: Map<String, String> = emptyMap()) :
        this(status, body.toByteArray(Charsets.UTF_8), "application/json", headers)

    val text: String get() = String(body, Charsets.UTF_8)

    companion object {
        fun asset(asset: StaticAsset) = HttpResult(200, asset.bytes, asset.contentType, mapOf("Cache-Control" to "no-cache"))
    }
}
