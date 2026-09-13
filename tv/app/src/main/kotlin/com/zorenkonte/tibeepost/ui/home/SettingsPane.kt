package com.zorenkonte.tibeepost.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.model.Position
import com.zorenkonte.tibeepost.ui.ColorPresets
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.ActionRow
import com.zorenkonte.tibeepost.ui.components.ChoiceRow
import com.zorenkonte.tibeepost.ui.components.SliderRow
import com.zorenkonte.tibeepost.ui.components.SwatchRow
import com.zorenkonte.tibeepost.ui.components.SwitchRow
import com.zorenkonte.tibeepost.ui.theme.tvFocusFrame

@Composable
fun SettingsPane(state: TibeeUiState, actions: TibeeActions, onRerunSetup: () -> Unit, modifier: Modifier = Modifier) {
    var section by rememberSaveable { mutableStateOf(SettingsSection.CARD) }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryTabRow(selectedTabIndex = section.ordinal, containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                SettingsSection.entries.forEach { entry ->
                    Tab(
                        selected = entry == section,
                        onClick = { section = entry },
                        modifier = Modifier.tvFocusFrame(MaterialTheme.shapes.small, focusedScale = 1f),
                        text = { Text(stringResource(entry.titleRes)) },
                    )
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                when (section) {
                    SettingsSection.CARD -> cardRows(state, actions)
                    SettingsSection.APPEARANCE -> appearanceRows(state, actions)
                    SettingsSection.SOUND -> soundRows(state, actions)
                    SettingsSection.SECURITY -> securityRows(state, actions)
                    SettingsSection.STARTUP -> startupRows(state, actions, onRerunSetup)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.cardRows(state: TibeeUiState, actions: TibeeActions) {
    item {
        ActionRow(
            title = stringResource(R.string.row_test_title),
            subtitle = state.testResult.ifEmpty { stringResource(R.string.row_test_body) },
            actionLabel = stringResource(R.string.action_send),
            onClick = actions::sendTest,
        )
    }
    item {
        SliderRow(
            title = stringResource(R.string.row_width),
            valueLabel = "${state.widthPercent}%",
            value = state.widthPercent.toFloat(),
            range = 10f..100f,
            step = 5f,
            onChange = { actions.changeWidth(Math.round(it / 5f) * 5) },
        )
    }
    item {
        SliderRow(
            title = stringResource(R.string.row_duration),
            valueLabel = "${state.durationSeconds} s",
            value = state.durationSeconds.toFloat(),
            range = 1f..120f,
            step = 1f,
            onChange = { actions.changeDuration(Math.round(it)) },
        )
    }
    item {
        SliderRow(
            title = stringResource(R.string.row_dim),
            valueLabel = "${Math.round(state.dim * 100)}%",
            value = state.dim,
            range = 0f..1f,
            step = 0.05f,
            onChange = { actions.changeDim(Math.round(it * 20f) / 20f) },
        )
    }
    item {
        ChoiceRow(
            title = stringResource(R.string.row_position),
            options = Position.entries.map { it.wire to it.wire.replace('-', ' ') },
            selected = state.position,
            onSelect = actions::choosePosition,
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.appearanceRows(state: TibeeUiState, actions: TibeeActions) {
    item {
        SwatchRow(
            title = stringResource(R.string.row_background),
            presets = ColorPresets.opaque.map { it.name to it.hex },
            selectedHex = state.background,
            onSelect = actions::chooseBackground,
        )
    }
    item {
        SwatchRow(
            title = stringResource(R.string.row_text_color),
            presets = ColorPresets.text.map { it.name to it.hex },
            selectedHex = state.textColor,
            onSelect = actions::chooseTextColor,
        )
    }
    item {
        SwatchRow(
            title = stringResource(R.string.row_accent),
            presets = ColorPresets.accent.map { it.name to it.hex },
            selectedHex = state.accent,
            onSelect = actions::chooseAccent,
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.soundRows(state: TibeeUiState, actions: TibeeActions) {
    item {
        ChoiceRow(
            title = stringResource(R.string.row_sound),
            subtitle = stringResource(R.string.row_sound_body),
            options = listOf("default" to stringResource(R.string.sound_chime), "none" to stringResource(R.string.sound_none)),
            selected = state.sound,
            onSelect = actions::chooseSound,
        )
    }
    item {
        ActionRow(
            title = stringResource(R.string.sound_preview),
            subtitle = stringResource(R.string.sound_preview_body),
            actionLabel = stringResource(R.string.action_play),
            onClick = actions::previewChime,
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.securityRows(state: TibeeUiState, actions: TibeeActions) {
    item {
        ActionRow(
            title = stringResource(R.string.row_token),
            subtitle = state.token.ifEmpty { stringResource(R.string.row_token_off) },
            actionLabel = if (state.token.isEmpty()) stringResource(R.string.token_generate) else stringResource(R.string.token_regenerate),
            onClick = actions::generateToken,
        )
    }
    if (state.token.isNotEmpty()) {
        item {
            ActionRow(
                title = stringResource(R.string.token_clear),
                subtitle = stringResource(R.string.token_clear_body),
                actionLabel = stringResource(R.string.token_clear),
                onClick = actions::clearToken,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.startupRows(state: TibeeUiState, actions: TibeeActions, onRerunSetup: () -> Unit) {
    item {
        SwitchRow(
            title = stringResource(R.string.row_autostart),
            subtitle = stringResource(R.string.row_autostart_body),
            checked = state.autostart,
            onToggle = actions::toggleAutostart,
        )
    }
    item {
        ActionRow(
            title = stringResource(R.string.row_rerun_setup),
            subtitle = stringResource(R.string.checklist_rerun_detail),
            actionLabel = stringResource(R.string.checklist_open),
            onClick = onRerunSetup,
        )
    }
}
