package com.zorenkonte.tibeepost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.zorenkonte.tibeepost.service.ServiceStarter
import com.zorenkonte.tibeepost.settings.Settings
import com.zorenkonte.tibeepost.ui.SettingsScreen
import com.zorenkonte.tibeepost.ui.TibeeTheme
import com.zorenkonte.tibeepost.ui.onboarding.OnboardingScreen
import com.zorenkonte.tibeepost.ui.rememberSettingsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceStarter.start(this)
        val setupPending = !Settings(this).onboardingDone
        setContent {
            TibeeTheme {
                val state = rememberSettingsState()
                var showSetup by rememberSaveable { mutableStateOf(setupPending) }
                if (showSetup) {
                    OnboardingScreen(state, onFinished = { showSetup = false })
                } else {
                    SettingsScreen(state, onRerunSetup = { showSetup = true })
                }
            }
        }
    }
}
