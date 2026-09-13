package com.zorenkonte.tibeepost.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.model.HexColor
import com.zorenkonte.tibeepost.ui.theme.tvFocusFrame

@Composable
fun SettingRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    onAdjust: ((Int) -> Unit)? = null,
    below: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    val shape = MaterialTheme.shapes.large
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .tvFocusFrame(shape, focusedScale = 1.01f)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || onAdjust == null) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionLeft -> {
                        onAdjust(-1)
                        true
                    }
                    Key.DirectionRight -> {
                        onAdjust(1)
                        true
                    }
                    else -> false
                }
            }
            .clickable(onClick = onClick),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                trailing()
            }
            below?.invoke()
        }
    }
}

@Composable
fun SliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clamp = { v: Float -> v.coerceIn(range.start, range.endInclusive) }
    SettingRow(
        title = title,
        modifier = modifier,
        onAdjust = { direction -> onChange(clamp(value + direction * step)) },
        trailing = { ValuePill(valueLabel) },
        below = {
            Slider(
                value = value,
                onValueChange = { onChange(clamp(it)) },
                valueRange = range,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .focusProperties { canFocus = false },
            )
        },
    )
}

@Composable
fun ChoiceRow(
    title: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val index = options.indexOfFirst { it.first == selected }.coerceAtLeast(0)
    val step = { direction: Int -> onSelect(options[((index + direction) % options.size + options.size) % options.size].first) }
    SettingRow(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onAdjust = step,
        onClick = { step(1) },
        trailing = { ValuePill("◀  ${options[index].second}  ▶") },
    )
}

@Composable
fun SwatchRow(
    title: String,
    presets: List<Pair<String, String>>,
    selectedHex: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val index = presets.indexOfFirst { it.second.equals(selectedHex, ignoreCase = true) }.coerceAtLeast(0)
    val step = { direction: Int -> onSelect(presets[((index + direction) % presets.size + presets.size) % presets.size].second) }
    SettingRow(
        title = title,
        subtitle = presets[index].first,
        modifier = modifier,
        onAdjust = step,
        onClick = { step(1) },
        trailing = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                presets.forEachIndexed { i, (_, hex) ->
                    Swatch(hex, selected = i == index)
                }
            }
        },
    )
}

@Composable
fun SwitchRow(
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    SettingRow(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onClick = { onToggle(!checked) },
        onAdjust = { direction -> onToggle(direction > 0) },
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                modifier = Modifier.focusProperties { canFocus = false },
            )
        },
    )
}

@Composable
fun ActionRow(
    title: String,
    actionLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    SettingRow(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onClick = onClick,
        trailing = { ValuePill(actionLabel, emphasized = true) },
    )
}

@Composable
fun ValuePill(text: String, emphasized: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (emphasized) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (emphasized) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun Swatch(hex: String, selected: Boolean) {
    val argb = HexColor.parse(hex)
    val fill = if (argb == null) Color.Transparent else Color(argb)
    val ring = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Box(
        Modifier
            .size(if (selected) 26.dp else 20.dp)
            .background(fill, CircleShape)
            .border(if (selected) 3.dp else 1.dp, ring, CircleShape),
    )
}
