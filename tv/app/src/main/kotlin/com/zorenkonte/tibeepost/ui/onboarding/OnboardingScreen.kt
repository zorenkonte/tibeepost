package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState

@Composable
fun OnboardingScreen(state: TibeeUiState, actions: TibeeActions, onFinished: () -> Unit) {
    var step by rememberSaveable { mutableStateOf(OnboardingStep.WELCOME) }
    var permissionSkipped by rememberSaveable { mutableStateOf(false) }
    val primaryFocus = remember { FocusRequester() }

    LaunchedEffect(step) { primaryFocus.requestFocus() }

    val permittedOnEntry = remember(step) { state.overlayPermitted }
    LaunchedEffect(step, state.overlayPermitted) {
        if (step == OnboardingStep.PERMISSION && !permittedOnEntry && state.overlayPermitted) {
            permissionSkipped = false
            step = OnboardingStep.TEST
        }
    }

    when (step) {
        OnboardingStep.WELCOME -> WelcomeStep(state, primaryFocus, onContinue = { step = OnboardingStep.PERMISSION })
        OnboardingStep.PERMISSION -> PermissionStep(
            state = state,
            actions = actions,
            primaryFocus = primaryFocus,
            onContinue = {
                permissionSkipped = false
                step = OnboardingStep.TEST
            },
            onSkip = {
                permissionSkipped = true
                step = OnboardingStep.TEST
            },
        )
        OnboardingStep.TEST -> TestStep(
            state = state,
            actions = actions,
            permissionMissing = permissionSkipped && !state.overlayPermitted,
            primaryFocus = primaryFocus,
            onBackToPermission = { step = OnboardingStep.PERMISSION },
            onContinue = { step = OnboardingStep.DONE },
        )
        OnboardingStep.DONE -> DoneStep(
            state = state,
            primaryFocus = primaryFocus,
            onFinish = {
                actions.completeOnboarding()
                onFinished()
            },
        )
    }
}
