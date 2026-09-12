package com.zorenkonte.tibeepost.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R

@Composable
fun StatusPanel(state: SettingsState, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(R.string.status_send_to), style = MaterialTheme.typography.titleMedium, color = Color(0xFF9CA3AF))
        Text(
            text = "http://${state.ipAddress ?: stringResource(R.string.status_no_network)}:${state.port}",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 52.sp, lineHeight = 60.sp),
        )
        Spacer(Modifier.height(8.dp))
        StatusLine(
            label = stringResource(R.string.status_server),
            value = when (state.serverReachable) {
                null -> stringResource(R.string.status_checking)
                true -> stringResource(R.string.status_running)
                false -> stringResource(R.string.status_not_running)
            },
            good = state.serverReachable == true,
        )
        StatusLine(
            label = stringResource(R.string.status_auth),
            value = if (state.token.isEmpty()) stringResource(R.string.status_auth_off) else stringResource(R.string.status_auth_on),
            good = true,
        )
        StatusLine(
            label = stringResource(R.string.status_overlay),
            value = if (state.overlayPermitted) stringResource(R.string.status_granted) else stringResource(R.string.status_missing),
            good = state.overlayPermitted,
        )
        if (!state.overlayPermitted) {
            Spacer(Modifier.height(8.dp))
            OverlayPermissionPanel(packageName = state.packageName)
        }
    }
}

@Composable
private fun StatusLine(label: String, value: String, good: Boolean) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.titleLarge,
        color = if (good) Color(0xFF34D399) else Color(0xFFF87171),
    )
}
