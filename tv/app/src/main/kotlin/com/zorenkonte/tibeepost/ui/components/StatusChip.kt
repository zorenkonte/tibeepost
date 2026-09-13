package com.zorenkonte.tibeepost.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.ui.theme.TibeeAmber
import com.zorenkonte.tibeepost.ui.theme.TibeeGreen
import com.zorenkonte.tibeepost.ui.theme.TibeeRed

enum class Tone { GOOD, BAD, NEUTRAL }

@Composable
fun StatusChip(label: String, tone: Tone, modifier: Modifier = Modifier) {
    val dot = when (tone) {
        Tone.GOOD -> TibeeGreen
        Tone.BAD -> TibeeRed
        Tone.NEUTRAL -> TibeeAmber
    }
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(9.dp).background(dot, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun tone(good: Boolean?): Tone = when (good) {
    true -> Tone.GOOD
    false -> Tone.BAD
    null -> Tone.NEUTRAL
}

val Tone.color: Color
    get() = when (this) {
        Tone.GOOD -> TibeeGreen
        Tone.BAD -> TibeeRed
        Tone.NEUTRAL -> TibeeAmber
    }
