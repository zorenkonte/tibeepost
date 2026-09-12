package com.zorenkonte.tibeepost.model

import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

class NotificationPayloadParser(
    private val generateId: () -> String = { UUID.randomUUID().toString() },
) {
    private class FieldError(message: String) : Exception(message)

    fun parse(body: String, defaults: NotificationDefaults): ParseResult {
        val json = try {
            JSONObject(body.ifBlank { throw JSONException("empty") })
        } catch (_: JSONException) {
            return ParseResult.Failure("body is not a JSON object")
        }
        return try {
            ParseResult.Success(build(json, defaults))
        } catch (e: FieldError) {
            ParseResult.Failure(e.message ?: "invalid payload")
        }
    }

    private fun build(json: JSONObject, defaults: NotificationDefaults): Notification {
        val message = string(json, "message")?.trim()?.takeIf { it.isNotEmpty() }
            ?: throw FieldError("field 'message' is required")
        return Notification(
            id = string(json, "id")?.trim()?.also { validateId(it) } ?: generateId(),
            title = string(json, "title")?.trim()?.take(MAX_TITLE)?.takeIf { it.isNotEmpty() },
            message = message.take(MAX_MESSAGE),
            imageUrl = url(json, "image"),
            iconUrl = url(json, "icon"),
            durationSeconds = int(json, "duration", 1, MAX_DURATION) ?: defaults.durationSeconds,
            persistent = boolean(json, "persistent") ?: false,
            position = position(json) ?: defaults.position,
            widthPercent = int(json, "widthPercent", 10, 100) ?: defaults.widthPercent,
            background = color(json, "background") ?: defaults.background,
            textColor = color(json, "textColor") ?: defaults.textColor,
            accent = color(json, "accent") ?: defaults.accent,
            dim = fraction(json, "dim") ?: defaults.dim,
            sound = sound(json) ?: defaults.sound,
            speak = boolean(json, "speak") ?: false,
        )
    }

    private fun validateId(id: String) {
        if (id.isEmpty()) throw FieldError("field 'id' must not be empty")
        if (id.length > MAX_ID) throw FieldError("field 'id' must be at most $MAX_ID characters")
        if (id.contains('/')) throw FieldError("field 'id' must not contain '/'")
    }

    private fun present(json: JSONObject, key: String) = json.has(key) && !json.isNull(key)

    private fun string(json: JSONObject, key: String): String? {
        if (!present(json, key)) return null
        return json.get(key) as? String ?: throw FieldError("field '$key' must be a string")
    }

    private fun boolean(json: JSONObject, key: String): Boolean? {
        if (!present(json, key)) return null
        return json.get(key) as? Boolean ?: throw FieldError("field '$key' must be true or false")
    }

    private fun number(json: JSONObject, key: String): Double? {
        if (!present(json, key)) return null
        return (json.get(key) as? Number)?.toDouble() ?: throw FieldError("field '$key' must be a number")
    }

    private fun int(json: JSONObject, key: String, min: Int, max: Int): Int? {
        val value = number(json, key) ?: return null
        if (value < min || value > max) throw FieldError("field '$key' must be between $min and $max")
        return value.toInt()
    }

    private fun fraction(json: JSONObject, key: String): Float? {
        val value = number(json, key) ?: return null
        if (value < 0.0 || value > 1.0) throw FieldError("field '$key' must be between 0 and 1")
        return value.toFloat()
    }

    private fun url(json: JSONObject, key: String): String? {
        val value = string(json, key)?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            throw FieldError("field '$key' must be an http or https URL")
        }
        return value
    }

    private fun color(json: JSONObject, key: String): Int? {
        val value = string(json, key)?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return HexColor.parse(value) ?: throw FieldError("field '$key' must be a hex color like #RRGGBB")
    }

    private fun position(json: JSONObject): Position? {
        val value = string(json, "position") ?: return null
        return Position.fromWire(value)
            ?: throw FieldError("field 'position' must be one of ${Position.entries.joinToString { it.wire }}")
    }

    private fun sound(json: JSONObject): SoundSpec? {
        val value = string(json, "sound")?.trim() ?: return null
        return SoundSpec.fromWire(value)
            ?: throw FieldError("field 'sound' must be 'none', 'default' or an http(s) URL")
    }

    private companion object {
        const val MAX_ID = 128
        const val MAX_TITLE = 200
        const val MAX_MESSAGE = 2000
        const val MAX_DURATION = 3600
    }
}
