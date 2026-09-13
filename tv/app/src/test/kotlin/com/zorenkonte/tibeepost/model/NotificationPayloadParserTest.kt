package com.zorenkonte.tibeepost.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPayloadParserTest {
    private val parser = NotificationPayloadParser { "generated" }
    private val defaults = NotificationDefaults()

    private fun success(body: String, withDefaults: NotificationDefaults = defaults): Notification =
        (parser.parse(body, withDefaults) as ParseResult.Success).notification

    private fun failure(body: String): String = (parser.parse(body, defaults) as ParseResult.Failure).message

    @Test
    fun messageOnlyGetsEveryDefault() {
        val n = success("""{"message":"hello"}""")
        assertEquals("generated", n.id)
        assertNull(n.title)
        assertEquals("hello", n.message)
        assertEquals(15, n.durationSeconds)
        assertEquals(false, n.persistent)
        assertEquals(Position.CENTER, n.position)
        assertEquals(60, n.widthPercent)
        assertEquals(0xFFFFFFFF.toInt(), n.background)
        assertEquals(0xFF111111.toInt(), n.textColor)
        assertNull(n.accent)
        assertEquals(0f, n.dim)
        assertEquals(SoundSpec.Default, n.sound)
        assertEquals(false, n.speak)
    }

    @Test
    fun settingsDefaultsApplyWhenFieldsOmitted() {
        val custom = NotificationDefaults(
            durationSeconds = 5,
            position = Position.TOP_RIGHT,
            widthPercent = 40,
            dim = 0.5f,
            sound = SoundSpec.None,
        )
        val n = success("""{"message":"hi"}""", custom)
        assertEquals(5, n.durationSeconds)
        assertEquals(Position.TOP_RIGHT, n.position)
        assertEquals(40, n.widthPercent)
        assertEquals(0.5f, n.dim)
        assertEquals(SoundSpec.None, n.sound)
    }

    @Test
    fun fullPayloadIsParsed() {
        val n = success(
            """{
              "id":"order-1042","title":"Order #1042","message":"New order received",
              "image":"https://example.com/banner.png","icon":"https://example.com/icon.png",
              "duration":15,"persistent":true,"position":"bottom-left","widthPercent":80,
              "background":"#FFFFFF","textColor":"#111111","accent":"#FF1744","dim":0.95,
              "sound":"https://example.com/ding.mp3","speak":true
            }""",
        )
        assertEquals("order-1042", n.id)
        assertEquals("Order #1042", n.title)
        assertEquals("https://example.com/banner.png", n.imageUrl)
        assertEquals("https://example.com/icon.png", n.iconUrl)
        assertEquals(true, n.persistent)
        assertEquals(Position.BOTTOM_LEFT, n.position)
        assertEquals(80, n.widthPercent)
        assertEquals(0xFFFF1744.toInt(), n.accent)
        assertEquals(0.95f, n.dim)
        assertEquals(SoundSpec.Url("https://example.com/ding.mp3"), n.sound)
        assertEquals(true, n.speak)
    }

    @Test
    fun invalidJsonIsRejected() {
        assertEquals("body is not a JSON object", failure("{not json"))
        assertEquals("body is not a JSON object", failure(""))
        assertEquals("body is not a JSON object", failure("[1,2]"))
    }

    @Test
    fun missingMessageIsRejected() {
        assertEquals("field 'message' is required", failure("""{"title":"x"}"""))
        assertEquals("field 'message' is required", failure("""{"message":"   "}"""))
        assertEquals("field 'message' is required", failure("""{"message":null}"""))
    }

    @Test
    fun wrongTypesNameTheField() {
        assertEquals("field 'message' must be a string", failure("""{"message":42}"""))
        assertEquals("field 'duration' must be a number", failure("""{"message":"m","duration":"15"}"""))
        assertEquals("field 'persistent' must be true or false", failure("""{"message":"m","persistent":"yes"}"""))
        assertEquals("field 'title' must be a string", failure("""{"message":"m","title":["a"]}"""))
    }

    @Test
    fun rangesAreEnforced() {
        assertEquals("field 'duration' must be between 1 and 3600", failure("""{"message":"m","duration":0}"""))
        assertEquals("field 'widthPercent' must be between 10 and 100", failure("""{"message":"m","widthPercent":150}"""))
        assertEquals("field 'dim' must be between 0 and 1", failure("""{"message":"m","dim":1.5}"""))
    }

    @Test
    fun enumsAndColorsAreValidated() {
        assertTrue(failure("""{"message":"m","position":"middle"}""").startsWith("field 'position' must be one of"))
        assertEquals("field 'background' must be a hex color like #RRGGBB", failure("""{"message":"m","background":"red"}"""))
        assertEquals("field 'sound' must be 'none', 'default' or an http(s) URL", failure("""{"message":"m","sound":"beep"}"""))
        assertEquals("field 'image' must be an http or https URL", failure("""{"message":"m","image":"ftp://x"}"""))
    }

    @Test
    fun idIsValidated() {
        assertEquals("field 'id' must not be empty", failure("""{"message":"m","id":"  "}"""))
        assertEquals("field 'id' must not contain '/'", failure("""{"message":"m","id":"a/b"}"""))
    }

    @Test
    fun longTextIsTruncated() {
        val n = success("""{"message":"${"m".repeat(3000)}","title":"${"t".repeat(300)}"}""")
        assertEquals(2000, n.message.length)
        assertEquals(200, n.title!!.length)
    }

    @Test
    fun explicitEmptyAccentOverridesTheDefault() {
        val withAccent = NotificationDefaults(accent = 0xFFFF1744.toInt())
        assertEquals(0xFFFF1744.toInt(), success("""{"message":"m"}""", withAccent).accent)
        assertNull(success("""{"message":"m","accent":""}""", withAccent).accent)
        assertNull(success("""{"message":"m","accent":"none"}""", withAccent).accent)
        assertNull(success("""{"message":"m","accent":null}""", withAccent).accent)
        assertEquals(0xFF00C853.toInt(), success("""{"message":"m","accent":"#00C853"}""", withAccent).accent)
    }

    @Test
    fun invalidAccentNamesTheField() {
        assertTrue(failure("""{"message":"m","accent":"stripe"}""").startsWith("field 'accent' must be"))
        assertTrue(failure("""{"message":"m","accent":12}""").startsWith("field 'accent' must be"))
    }

    @Test
    fun blankOptionalStringsCountAsAbsent() {
        val n = success("""{"message":"m","image":"","accent":"","title":""}""")
        assertNull(n.imageUrl)
        assertNull(n.accent)
        assertNull(n.title)
    }
}
