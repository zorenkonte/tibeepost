package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.SectionCard

@Composable
fun WelcomeStep(state: TibeeUiState, primaryFocus: FocusRequester, onContinue: () -> Unit) {
    SetupScaffold(stepNumber = 1, title = stringResource(R.string.setup_welcome_title)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionCard(title = stringResource(R.string.welcome_card_what), modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.setup_welcome_line1), style = MaterialTheme.typography.bodyLarge)
            }
            SectionCard(title = stringResource(R.string.welcome_card_address), modifier = Modifier.weight(1f)) {
                Text(state.address, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.welcome_address_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SectionCard(title = stringResource(R.string.welcome_card_permission), modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.setup_welcome_line3), style = MaterialTheme.typography.bodyLarge)
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PrimaryStepButton(stringResource(R.string.setup_continue), onContinue, primaryFocus)
        }
    }
}
