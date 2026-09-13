package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.QrCode
import com.zorenkonte.tibeepost.ui.components.SectionCard

@Composable
fun DoneStep(state: TibeeUiState, primaryFocus: FocusRequester, onFinish: () -> Unit) {
    val usable = state.overlayPermitted
    SetupScaffold(
        stepNumber = null,
        title = stringResource(if (usable) R.string.setup_done_title else R.string.setup_done_unusable_title),
    ) {
        if (!usable) UnusableWarning(packageName = state.packageName)
        SectionCard(title = stringResource(R.string.setup_done_address)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(state.address, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    Text(stringResource(R.string.setup_done_body), style = MaterialTheme.typography.bodyLarge)
                }
                if (state.ipAddress != null) QrCode(content = "${state.address}/", size = 132.dp)
            }
        }
        PrimaryStepButton(stringResource(if (usable) R.string.setup_finish else R.string.setup_finish_anyway), onFinish, primaryFocus)
    }
}
