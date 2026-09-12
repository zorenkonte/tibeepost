package com.zorenkonte.tibeepost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.model.Position
import com.zorenkonte.tibeepost.sound.ChimePlayer
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(state: SettingsState = rememberSettingsState()) {
    val context = LocalContext.current
    val chime = remember { ChimePlayer(context) }
    DisposableEffect(chime) { onDispose { chime.release() } }
    val scope = rememberCoroutineScope()
    val firstRow = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstRow.requestFocus() }

    Row(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
            .padding(horizontal = 48.dp, vertical = 40.dp),
    ) {
        StatusPanel(state, Modifier.weight(0.45f))
        Spacer(Modifier.width(40.dp))
        LazyColumn(Modifier.weight(0.55f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                ListItem(
                    selected = false,
                    onClick = { scope.launch { state.testResult = TestNotificationSender.send(state.token) } },
                    modifier = Modifier.focusRequester(firstRow),
                    headlineContent = { Text(stringResource(R.string.row_test_title), style = MaterialTheme.typography.titleLarge) },
                    supportingContent = {
                        Text(state.testResult.ifEmpty { stringResource(R.string.row_test_body) }, style = MaterialTheme.typography.bodyMedium)
                    },
                )
            }
            item { TokenRow(state) }
            item {
                ListItem(
                    selected = false,
                    onClick = { state.updateAutostart(!state.autostart) },
                    headlineContent = { Text(stringResource(R.string.row_autostart), style = MaterialTheme.typography.titleLarge) },
                    supportingContent = { Text(stringResource(R.string.row_autostart_body), style = MaterialTheme.typography.bodyMedium) },
                    trailingContent = { Switch(checked = state.autostart, onCheckedChange = null) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_sound),
                    value = if (state.sound == "none") stringResource(R.string.sound_none) else stringResource(R.string.sound_chime),
                    onDecrease = state::toggleSound,
                    onIncrease = state::toggleSound,
                    decoration = {
                        Button(onClick = chime::play) { Text(stringResource(R.string.sound_preview)) }
                    },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_width),
                    value = "${state.widthPercent}%",
                    onDecrease = { state.stepWidth(-1) },
                    onIncrease = { state.stepWidth(1) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_duration),
                    value = "${state.durationSeconds} s",
                    onDecrease = { state.stepDuration(-1) },
                    onIncrease = { state.stepDuration(1) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_dim),
                    value = "${Math.round(state.dim * 100)}%",
                    onDecrease = { state.stepDim(-1) },
                    onIncrease = { state.stepDim(1) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_position),
                    value = Position.fromWire(state.position)?.wire ?: state.position,
                    onDecrease = { state.stepPosition(-1) },
                    onIncrease = { state.stepPosition(1) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_background),
                    value = ColorPresets.nameOf(ColorPresets.opaque, state.background),
                    onDecrease = { state.stepBackground(-1) },
                    onIncrease = { state.stepBackground(1) },
                    decoration = { ColorSwatch(state.background) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_text_color),
                    value = ColorPresets.nameOf(ColorPresets.text, state.textColor),
                    onDecrease = { state.stepTextColor(-1) },
                    onIncrease = { state.stepTextColor(1) },
                    decoration = { ColorSwatch(state.textColor) },
                )
            }
            item {
                StepperRow(
                    title = stringResource(R.string.row_accent),
                    value = ColorPresets.nameOf(ColorPresets.accent, state.accent),
                    onDecrease = { state.stepAccent(-1) },
                    onIncrease = { state.stepAccent(1) },
                    decoration = { ColorSwatch(state.accent) },
                )
            }
        }
    }
}
