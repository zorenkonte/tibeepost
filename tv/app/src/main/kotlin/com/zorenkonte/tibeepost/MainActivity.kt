package com.zorenkonte.tibeepost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zorenkonte.tibeepost.service.ServiceStarter
import com.zorenkonte.tibeepost.ui.SettingsScreen
import com.zorenkonte.tibeepost.ui.TibeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceStarter.start(this)
        setContent {
            TibeeTheme { SettingsScreen() }
        }
    }
}
