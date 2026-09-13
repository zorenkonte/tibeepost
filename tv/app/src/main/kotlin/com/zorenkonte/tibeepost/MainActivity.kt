package com.zorenkonte.tibeepost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.zorenkonte.tibeepost.service.ServiceStarter
import com.zorenkonte.tibeepost.settings.Settings
import com.zorenkonte.tibeepost.ui.home.HomeScreen
import com.zorenkonte.tibeepost.ui.onboarding.OnboardingScreen
import com.zorenkonte.tibeepost.ui.permission.PermissionGuideScreen
import com.zorenkonte.tibeepost.ui.rememberSettingsState
import com.zorenkonte.tibeepost.ui.theme.TibeeTheme

enum class Screen { SETUP, HOME, PERMISSION_GUIDE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceStarter.start(this)
        val setupPending = !Settings(this).onboardingDone
        setContent {
            TibeeTheme {
                val settings = rememberSettingsState()
                val state = settings.snapshot()
                var screen by rememberSaveable { mutableStateOf(if (setupPending) Screen.SETUP else Screen.HOME) }
                BackHandler(enabled = screen == Screen.PERMISSION_GUIDE) { screen = Screen.HOME }
                when (screen) {
                    Screen.SETUP -> OnboardingScreen(state, settings, onFinished = { screen = Screen.HOME })
                    Screen.HOME -> HomeScreen(
                        state = state,
                        actions = settings,
                        onFixPermission = { screen = Screen.PERMISSION_GUIDE },
                        onRerunSetup = {
                            settings.resetOnboarding()
                            screen = Screen.SETUP
                        },
                    )
                    Screen.PERMISSION_GUIDE -> PermissionGuideScreen(state, settings, onBack = { screen = Screen.HOME })
                }
            }
        }
    }
}
