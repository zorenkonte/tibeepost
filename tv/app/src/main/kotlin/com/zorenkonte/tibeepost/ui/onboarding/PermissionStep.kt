package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.permission.PermissionGuide

@Composable
fun PermissionStep(
    state: TibeeUiState,
    actions: TibeeActions,
    primaryFocus: FocusRequester,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    SetupScaffold(
        stepNumber = 2,
        title = if (state.overlayPermitted) stringResource(R.string.setup_permission_granted_title) else stringResource(R.string.setup_permission_title),
    ) {
        PermissionGuide(
            state = state,
            actions = actions,
            primaryFocus = primaryFocus,
            footer = { SecondaryStepButton(stringResource(R.string.setup_skip), onSkip) },
        )
        if (state.overlayPermitted) {
            PrimaryStepButton(stringResource(R.string.setup_continue), onContinue, primaryFocus)
        }
    }
}
