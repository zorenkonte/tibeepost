package com.zorenkonte.tibeepost.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.StatusChip
import com.zorenkonte.tibeepost.ui.components.tone

@Composable
fun HomeScreen(
    state: TibeeUiState,
    actions: TibeeActions,
    onFixPermission: () -> Unit,
    onRerunSetup: () -> Unit,
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(14.dp))
            Text(
                stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(
                    label = when (state.serverReachable) {
                        true -> stringResource(R.string.chip_server_on)
                        false -> stringResource(R.string.chip_server_off)
                        null -> stringResource(R.string.chip_server_checking)
                    },
                    tone = tone(state.serverReachable),
                )
                StatusChip(
                    label = if (state.overlayPermitted) stringResource(R.string.chip_overlay_on) else stringResource(R.string.chip_overlay_off),
                    tone = tone(state.overlayPermitted),
                )
                StatusChip(
                    label = if (state.authEnabled) stringResource(R.string.chip_auth_on) else stringResource(R.string.chip_auth_off),
                    tone = tone(if (state.authEnabled) true else null),
                )
            }
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.width(360.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ConnectionCard(state)
                SetupChecklistCard(state, onFixPermission = onFixPermission, modifier = Modifier.weight(1f))
            }
            SettingsPane(state, actions, onRerunSetup, Modifier.weight(1f).fillMaxHeight())
        }
    }
    }
}
