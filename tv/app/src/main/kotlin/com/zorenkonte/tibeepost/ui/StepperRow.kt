package com.zorenkonte.tibeepost.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun StepperRow(
    title: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    decoration: (@Composable () -> Unit)? = null,
) {
    ListItem(
        selected = false,
        onClick = onIncrease,
        modifier = modifier.onKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
            when (event.key) {
                Key.DirectionLeft -> {
                    onDecrease()
                    true
                }
                Key.DirectionRight -> {
                    onIncrease()
                    true
                }
                else -> false
            }
        },
        headlineContent = { Text(title, style = MaterialTheme.typography.titleLarge) },
        supportingContent = supporting?.let { { Text(it, style = MaterialTheme.typography.bodyMedium) } },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                decoration?.let {
                    it()
                    Spacer(Modifier.width(12.dp))
                }
                Text("◀  $value  ▶", style = MaterialTheme.typography.titleLarge)
            }
        },
    )
}
