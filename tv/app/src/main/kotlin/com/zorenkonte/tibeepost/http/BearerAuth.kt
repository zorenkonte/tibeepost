package com.zorenkonte.tibeepost.http

import java.security.MessageDigest

object BearerAuth {
    fun isAuthorized(request: HttpRequest, expectedToken: String): Boolean {
        if (expectedToken.isEmpty()) return true
        val header = request.header("authorization") ?: return false
        val presented = header.removePrefix("Bearer ").removePrefix("bearer ").trim()
        if (presented == header.trim()) return false
        return MessageDigest.isEqual(presented.toByteArray(), expectedToken.toByteArray())
    }

    fun challenge(): HttpResult = JsonResponses.error(
        401,
        "missing or invalid bearer token",
        mapOf("WWW-Authenticate" to "Bearer"),
    )
}
