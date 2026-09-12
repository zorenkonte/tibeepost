package com.zorenkonte.tibeepost.http

import com.zorenkonte.tibeepost.model.NotificationDefaults
import com.zorenkonte.tibeepost.model.NotificationPayloadParser
import com.zorenkonte.tibeepost.model.ParseResult
import com.zorenkonte.tibeepost.queue.DismissResult
import com.zorenkonte.tibeepost.queue.NotificationSink
import com.zorenkonte.tibeepost.queue.SubmitResult
import org.json.JSONObject

class Router(
    private val info: ServerInfo,
    private val sink: NotificationSink,
    private val parser: NotificationPayloadParser,
    private val defaults: () -> NotificationDefaults,
) {

    fun handle(request: HttpRequest): HttpResult = try {
        route(request)
    } catch (e: Exception) {
        JsonResponses.error(500, e.message ?: e.javaClass.simpleName)
    }

    private fun route(request: HttpRequest): HttpResult {
        val path = request.path.trimEnd('/').ifEmpty { "/" }
        if (request.method == "OPTIONS") return CorsHeaders.preflight()
        return when {
            path == "/health" -> requireMethod(request, "GET") { health() }
            path == "/info" -> requireMethod(request, "GET") { info() }
            path == "/notify" -> requireMethod(request, "POST") { notify(request) }
            path.startsWith("/notify/") -> requireMethod(request, "DELETE") { clear(path.removePrefix("/notify/")) }
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

    private fun notify(request: HttpRequest): HttpResult {
        val notification = when (val parsed = parser.parse(request.body, defaults())) {
            is ParseResult.Failure -> return JsonResponses.error(400, parsed.message)
            is ParseResult.Success -> parsed.notification
        }
        val result = sink.submit(notification)
        val body = JSONObject().put("id", notification.id).put("result", result.wire)
        return when (result) {
            SubmitResult.NO_OVERLAY_PERMISSION -> HttpResult(
                503,
                body.put("error", "overlay permission missing; run: adb shell appops set ${info.packageName} SYSTEM_ALERT_WINDOW allow").toString(),
            )
            SubmitResult.FAILED -> HttpResult(500, body.put("error", "could not draw the overlay").toString())
            else -> JsonResponses.ok(body)
        }
    }

    private fun clear(id: String): HttpResult {
        if (id.isBlank()) return JsonResponses.error(400, "notification id is required in the path")
        val result = sink.dismiss(id)
        val body = JSONObject().put("id", id).put("result", result.wire)
        return if (result == DismissResult.UNKNOWN) HttpResult(404, body.toString()) else JsonResponses.ok(body)
    }
}
