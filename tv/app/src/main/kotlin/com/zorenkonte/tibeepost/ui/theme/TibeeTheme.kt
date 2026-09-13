package com.zorenkonte.tibeepost.ui.theme

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable

@Composable
fun TibeeTheme(content: @Composable () -> Unit) {
    MaterialExpressiveTheme(
        colorScheme = TibeeColorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}
