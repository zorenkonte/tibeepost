package com.zorenkonte.tibeepost.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.SettingsState

@Composable
fun PermissionStep(
    state: SettingsState,
    primaryFocus: FocusRequester,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    if (state.overlayPermitted) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.setup_permission_granted_title), style = MaterialTheme.typography.displayMedium)
            Text(stringResource(R.string.setup_permission_granted_body), style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onContinue, modifier = Modifier.focusRequester(primaryFocus)) {
                Text(stringResource(R.string.setup_continue))
            }
        }
        return
    }

    val context = LocalContext.current
    val systemSettings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${state.packageName}"))
    val canOpenSystemSettings = systemSettings.resolveActivity(context.packageManager) != null

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.setup_permission_title), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(R.string.setup_permission_body), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.setup_permission_system), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "adb shell appops set ${state.packageName} SYSTEM_ALERT_WINDOW allow",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
            color = Color(0xFFFDE68A),
        )
        Text(
            text = stringResource(R.string.setup_permission_recheck),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9CA3AF),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (canOpenSystemSettings) {
                Button(
                    onClick = { context.startActivity(systemSettings) },
                    modifier = Modifier.focusRequester(primaryFocus),
                ) {
                    Text(stringResource(R.string.overlay_open_settings))
                }
                Button(onClick = onSkip) { Text(stringResource(R.string.setup_skip)) }
            } else {
                Button(onClick = onSkip, modifier = Modifier.focusRequester(primaryFocus)) {
                    Text(stringResource(R.string.setup_skip))
                }
            }
        }
    }
}
