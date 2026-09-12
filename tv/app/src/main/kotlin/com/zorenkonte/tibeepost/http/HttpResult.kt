package com.zorenkonte.tibeepost.http

data class HttpResult(
    val status: Int,
    val body: String,
    val headers: Map<String, String> = emptyMap(),
)
