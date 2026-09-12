package com.zorenkonte.tibeepost.http

object CorsHeaders {
    val all: Map<String, String> = mapOf(
        "Access-Control-Allow-Origin" to "*",
        "Access-Control-Allow-Methods" to "GET, POST, DELETE, OPTIONS",
        "Access-Control-Allow-Headers" to "Content-Type, Authorization",
        "Access-Control-Max-Age" to "86400",
    )

    fun preflight() = HttpResult(204, "")
}
