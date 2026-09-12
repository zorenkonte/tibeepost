package com.zorenkonte.tibeepost.model

sealed class ParseResult {
    data class Success(val notification: Notification) : ParseResult()
    data class Failure(val message: String) : ParseResult()
}
