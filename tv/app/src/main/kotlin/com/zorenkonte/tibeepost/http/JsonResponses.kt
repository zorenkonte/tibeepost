package com.zorenkonte.tibeepost.http

import org.json.JSONObject

object JsonResponses {
    fun ok(body: JSONObject, headers: Map<String, String> = emptyMap()) = HttpResult(200, body.toString(), headers)

    fun error(status: Int, message: String, headers: Map<String, String> = emptyMap()) =
        HttpResult(status, JSONObject().put("error", message).toString(), headers)
}
