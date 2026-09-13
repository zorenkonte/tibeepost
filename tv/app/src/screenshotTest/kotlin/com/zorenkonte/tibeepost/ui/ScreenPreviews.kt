package com.zorenkonte.tibeepost.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.zorenkonte.tibeepost.ui.home.HomeScreen
import com.zorenkonte.tibeepost.ui.onboarding.DoneStep
import com.zorenkonte.tibeepost.ui.onboarding.PermissionStep
import com.zorenkonte.tibeepost.ui.onboarding.TestStep
import com.zorenkonte.tibeepost.ui.onboarding.WelcomeStep
import com.zorenkonte.tibeepost.ui.permission.PermissionGuideScreen
import com.zorenkonte.tibeepost.ui.theme.TibeeTheme

private val ready = TibeeUiState(
    ipAddress = "192.168.1.50",
    port = 8090,
    packageName = "com.zorenkonte.tibeepost",
    serverReachable = true,
    overlayPermitted = true,
    token = "",
    autostart = true,
    widthPercent = 60,
    durationSeconds = 15,
    dim = 0.3f,
    background = "#FFFFFF",
    textColor = "#111111",
    accent = "#FF1744",
    position = "center",
    sound = "default",
    testResult = "",
    lastOpenedSettings = null,
)

private val blocked = ready.copy(overlayPermitted = false, token = "k3yQz9fA2mN8pL4sT7vX1cB6dH0jW5rE", lastOpenedSettings = "SETTINGS")

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun HomeReadyPreview() {
    TibeeTheme { HomeScreen(ready, TibeeActions.None, onFixPermission = {}, onRerunSetup = {}) }
}

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun HomeBlockedPreview() {
    TibeeTheme { HomeScreen(blocked, TibeeActions.None, onFixPermission = {}, onRerunSetup = {}) }
}

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun PermissionGuidePreview() {
    TibeeTheme { PermissionGuideScreen(blocked, TibeeActions.None, onBack = {}) }
}

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun WelcomePreview() {
    TibeeTheme { WelcomeStep(ready, remember { FocusRequester() }, onContinue = {}) }
}

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun PermissionStepPreview() {
    TibeeTheme { PermissionStep(blocked, TibeeActions.None, remember { FocusRequester() }, onContinue = {}, onSkip = {}) }
}

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun TestStepBlockedPreview() {
    TibeeTheme {
        TestStep(
            blocked.copy(testResult = "HTTP 503"),
            TibeeActions.None,
            permissionMissing = true,
            primaryFocus = remember { FocusRequester() },
            onBackToPermission = {},
            onContinue = {},
        )
    }
}

@PreviewTest
@Preview(widthDp = 960, heightDp = 540)
@Composable
fun DonePreview() {
    TibeeTheme { DoneStep(ready, remember { FocusRequester() }, onFinish = {}) }
}
