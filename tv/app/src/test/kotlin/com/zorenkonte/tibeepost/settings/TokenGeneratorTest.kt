package com.zorenkonte.tibeepost.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenGeneratorTest {
    @Test
    fun tokensAreUrlSafeAndLong() {
        val token = TokenGenerator.generate()
        assertEquals(43, token.length)
        assertTrue(token.all { it.isLetterOrDigit() || it == '-' || it == '_' })
    }

    @Test
    fun tokensDiffer() {
        assertNotEquals(TokenGenerator.generate(), TokenGenerator.generate())
    }
}
