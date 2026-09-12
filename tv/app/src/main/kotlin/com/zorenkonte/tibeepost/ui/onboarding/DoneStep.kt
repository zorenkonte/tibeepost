package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.SettingsState

@Composable
fun DoneStep(state: SettingsState, primaryFocus: FocusRequester, onFinish: () -> Unit) {
    val usable = state.overlayPermitted
    val address = "http://${state.ipAddress ?: stringResource(R.string.status_no_network)}:${state.port}"

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(if (usable) R.string.setup_done_title else R.string.setup_done_unusable_title),
            style = MaterialTheme.typography.displayMedium,
        )
        if (!usable) UnusableWarning(packageName = state.packageName)
        Text(stringResource(R.string.setup_done_address), style = MaterialTheme.typography.titleMedium, color = Color(0xFF9CA3AF))
        Text(address, style = MaterialTheme.typography.displayMedium.copy(fontSize = 52.sp, lineHeight = 60.sp))
        Text(stringResource(R.string.setup_done_body), style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onFinish, modifier = Modifier.focusRequester(primaryFocus)) {
            Text(stringResource(if (usable) R.string.setup_finish else R.string.setup_finish_anyway))
        }
    }
}
