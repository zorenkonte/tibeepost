package com.zorenkonte.tibeepost.http

import org.json.JSONObject

class Router(private val info: ServerInfo) {

    fun handle(request: HttpRequest): HttpResult = try {
        route(request)
    } catch (e: Exception) {
        JsonResponses.error(500, e.message ?: e.javaClass.simpleName)
    }

    private fun route(request: HttpRequest): HttpResult {
        val path = request.path.trimEnd('/').ifEmpty { "/" }
        return when {
            path == "/health" -> requireMethod(request, "GET") { health() }
            path == "/info" -> requireMethod(request, "GET") { info() }
            else -> JsonResponses.error(404, "no route for ${request.method} $path")
        }
    }

    private fun requireMethod(request: HttpRequest, method: String, handler: () -> HttpResult): HttpResult =
        if (request.method == method) handler() else JsonResponses.error(405, "${request.method} not allowed here")

    private fun health() = JsonResponses.ok(
        JSONObject()
            .put("status", "ok")
            .put("app", "TibeePost")
            .put("version", info.appVersion),
    )

    private fun info() = JsonResponses.ok(
        JSONObject()
            .put("deviceName", info.deviceName)
            .put("ip", info.ipAddress ?: JSONObject.NULL)
            .put("port", info.port)
            .put("screenWidth", info.screenWidth)
            .put("screenHeight", info.screenHeight)
            .put("version", info.appVersion)
            .put("overlayPermission", info.overlayPermission),
    )
}
