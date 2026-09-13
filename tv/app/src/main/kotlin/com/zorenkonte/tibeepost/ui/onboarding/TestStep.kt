package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.theme.TibeeGreen
import com.zorenkonte.tibeepost.ui.theme.TibeeRed

@Composable
fun TestStep(
    state: TibeeUiState,
    actions: TibeeActions,
    permissionMissing: Boolean,
    primaryFocus: FocusRequester,
    onBackToPermission: () -> Unit,
    onContinue: () -> Unit,
) {
    SetupScaffold(stepNumber = 3, title = stringResource(R.string.setup_test_title)) {
        if (permissionMissing) UnusableWarning(packageName = null)
        Text(stringResource(R.string.setup_test_body), style = MaterialTheme.typography.bodyLarge)
        if (state.testResult.isNotEmpty()) {
            Text(
                text = if (permissionMissing) stringResource(R.string.setup_test_blocked) else state.testResult,
                style = MaterialTheme.typography.titleMedium,
                color = if (permissionMissing) TibeeRed else TibeeGreen,
            )
        }
        ButtonBar {
            PrimaryStepButton(stringResource(R.string.setup_test_send), actions::sendTest, primaryFocus)
            SecondaryStepButton(stringResource(R.string.setup_continue), onContinue)
            if (permissionMissing) SecondaryStepButton(stringResource(R.string.setup_back_to_permission), onBackToPermission)
        }
    }
}
