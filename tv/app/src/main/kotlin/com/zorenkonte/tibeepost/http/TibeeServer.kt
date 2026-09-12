package com.zorenkonte.tibeepost.http

import fi.iki.elonen.NanoHTTPD
import java.io.InputStream

class TibeeServer(port: Int, private val router: Router) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val contentLength = session.headers["content-length"]?.toLongOrNull() ?: 0L
        if (contentLength > ServerConfig.MAX_BODY_BYTES) {
            return toResponse(JsonResponses.error(413, "body larger than ${ServerConfig.MAX_BODY_BYTES} bytes"))
        }
        val body = readExactly(session.inputStream, contentLength.toInt())
        val request = HttpRequest(
            method = session.method.name,
            path = session.uri,
            headers = session.headers.mapKeys { it.key.lowercase() },
            body = body,
        )
        return toResponse(router.handle(request))
    }

    private fun toResponse(result: HttpResult): Response {
        val status = Response.Status.lookup(result.status) ?: Response.Status.INTERNAL_ERROR
        val response = if (result.body.isEmpty()) {
            newFixedLengthResponse(status, MIME_JSON, "")
        } else {
            newFixedLengthResponse(status, MIME_JSON, result.body)
        }
        result.headers.forEach { (name, value) -> response.addHeader(name, value) }
        return response
    }

    // The request stream never signals EOF, so reading beyond Content-Length blocks until the socket times out.
    private fun readExactly(input: InputStream, length: Int): String {
        if (length <= 0) return ""
        val bytes = ByteArray(length)
        var offset = 0
        while (offset < length) {
            val read = input.read(bytes, offset, length - offset)
            if (read < 0) break
            offset += read
        }
        return String(bytes, 0, offset, Charsets.UTF_8)
    }

    private companion object {
        const val MIME_JSON = "application/json"
    }
}
