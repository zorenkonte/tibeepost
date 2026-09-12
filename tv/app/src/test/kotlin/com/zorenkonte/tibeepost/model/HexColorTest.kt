package com.zorenkonte.tibeepost.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HexColorTest {
    @Test
    fun parsesSixDigitAsOpaque() {
        assertEquals(0xFF112233.toInt(), HexColor.parse("#112233"))
    }

    @Test
    fun parsesWithoutHash() {
        assertEquals(0xFFFF1744.toInt(), HexColor.parse("ff1744"))
    }

    @Test
    fun parsesThreeDigitShorthand() {
        assertEquals(0xFFFFAA00.toInt(), HexColor.parse("#FA0"))
    }

    @Test
    fun parsesEightDigitWithAlpha() {
        assertEquals(0x80000000.toInt(), HexColor.parse("#80000000"))
    }

    @Test
    fun parsesFourDigitWithAlpha() {
        assertEquals(0x88FF0000.toInt(), HexColor.parse("#8F00"))
    }

    @Test
    fun rejectsGarbage() {
        assertNull(HexColor.parse("red"))
        assertNull(HexColor.parse("#12345"))
        assertNull(HexColor.parse(""))
        assertNull(HexColor.parse("#GG0000"))
    }

    @Test
    fun formatsOpaqueAsSixDigits() {
        assertEquals("#FF1744", HexColor.format(0xFFFF1744.toInt()))
    }

    @Test
    fun formatsTranslucentAsEightDigits() {
        assertEquals("#80112233", HexColor.format(0x80112233.toInt()))
    }
}
