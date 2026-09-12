package com.zorenkonte.tibeepost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.model.HexColor

@Composable
fun ColorSwatch(hex: String) {
    val argb = HexColor.parse(hex)
    val shape = RoundedCornerShape(6.dp)
    Box(
        Modifier
            .size(28.dp)
            .clip(shape)
            .background(if (argb == null) Color.Transparent else Color(argb))
            .border(1.dp, Color.White.copy(alpha = 0.6f), shape),
    )
}
