package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.SettingsState

@Composable
fun WelcomeStep(state: SettingsState, primaryFocus: FocusRequester, onContinue: () -> Unit) {
    val address = "http://${state.ipAddress ?: stringResource(R.string.status_no_network)}:${state.port}"
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.setup_welcome_title), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(R.string.setup_welcome_line1), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.setup_welcome_line2, address), style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.setup_welcome_line3), style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onContinue, modifier = Modifier.focusRequester(primaryFocus)) {
            Text(stringResource(R.string.setup_continue))
        }
    }
}
