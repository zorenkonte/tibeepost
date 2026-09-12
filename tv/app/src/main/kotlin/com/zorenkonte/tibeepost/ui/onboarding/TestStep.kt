package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.SettingsState
import com.zorenkonte.tibeepost.ui.TestNotificationSender
import kotlinx.coroutines.launch

@Composable
fun TestStep(
    state: SettingsState,
    permissionMissing: Boolean,
    primaryFocus: FocusRequester,
    onBackToPermission: () -> Unit,
    onContinue: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (permissionMissing) UnusableWarning(packageName = null)
        Text(stringResource(R.string.setup_test_title), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(R.string.setup_test_body), style = MaterialTheme.typography.bodyLarge)
        if (state.testResult.isNotEmpty()) {
            Text(
                text = if (permissionMissing) stringResource(R.string.setup_test_blocked) else state.testResult,
                style = MaterialTheme.typography.titleMedium,
                color = if (permissionMissing) Color(0xFFFCA5A5) else Color(0xFF34D399),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { scope.launch { state.testResult = TestNotificationSender.send(state.token) } },
                modifier = Modifier.focusRequester(primaryFocus),
            ) {
                Text(stringResource(R.string.setup_test_send))
            }
            Button(onClick = onContinue) { Text(stringResource(R.string.setup_continue)) }
            if (permissionMissing) {
                Button(onClick = onBackToPermission) { Text(stringResource(R.string.setup_back_to_permission)) }
            }
        }
    }
}
