package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.SettingsState

@Composable
fun OnboardingScreen(state: SettingsState, onFinished: () -> Unit) {
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

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
            .padding(horizontal = 96.dp, vertical = 56.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (step != OnboardingStep.DONE) {
                Text(
                    text = stringResource(R.string.setup_step, step.number),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF9CA3AF),
                )
            }
            when (step) {
                OnboardingStep.WELCOME -> WelcomeStep(
                    state = state,
                    primaryFocus = primaryFocus,
                    onContinue = { step = OnboardingStep.PERMISSION },
                )
                OnboardingStep.PERMISSION -> PermissionStep(
                    state = state,
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
                    permissionMissing = permissionSkipped && !state.overlayPermitted,
                    primaryFocus = primaryFocus,
                    onBackToPermission = { step = OnboardingStep.PERMISSION },
                    onContinue = { step = OnboardingStep.DONE },
                )
                OnboardingStep.DONE -> DoneStep(
                    state = state,
                    primaryFocus = primaryFocus,
                    onFinish = {
                        state.completeOnboarding()
                        onFinished()
                    },
                )
            }
        }
    }
}
