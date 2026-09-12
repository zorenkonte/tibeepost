package com.zorenkonte.tibeepost.ui

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings as SystemSettings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.zorenkonte.tibeepost.bridge.NetworkAddress
import com.zorenkonte.tibeepost.http.ServerConfig
import com.zorenkonte.tibeepost.settings.Settings
import com.zorenkonte.tibeepost.settings.TokenGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SettingsState(context: Context) {
    private val appContext = context.applicationContext
    private val settings = Settings(appContext)

    var token by mutableStateOf(settings.token)
        private set
    var autostart by mutableStateOf(settings.autostart)
        private set
    var widthPercent by mutableStateOf(settings.widthPercent)
        private set
    var durationSeconds by mutableStateOf(settings.durationSeconds)
        private set
    var dim by mutableStateOf(settings.dim)
        private set
    var background by mutableStateOf(settings.background)
        private set
    var textColor by mutableStateOf(settings.textColor)
        private set
    var accent by mutableStateOf(settings.accent)
        private set
    var position by mutableStateOf(settings.position)
        private set
    var sound by mutableStateOf(settings.sound)
        private set

    var ipAddress by mutableStateOf(NetworkAddress.current())
        private set
    var overlayPermitted by mutableStateOf(SystemSettings.canDrawOverlays(appContext))
        private set
    var serverReachable by mutableStateOf<Boolean?>(null)
        private set
    var testResult by mutableStateOf("")

    val port: Int = ServerConfig.PORT
    val packageName: String = appContext.packageName

    val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> readPreferences() }

    fun observePreferences() = settings.observe(preferenceListener)

    fun stopObservingPreferences() = settings.stopObserving(preferenceListener)

    fun completeOnboarding() {
        settings.onboardingDone = true
    }

    fun resetOnboarding() {
        settings.onboardingDone = false
    }

    fun generateToken() {
        settings.token = TokenGenerator.generate()
    }

    fun clearToken() {
        settings.token = ""
    }

    fun updateAutostart(enabled: Boolean) {
        settings.autostart = enabled
    }

    fun stepWidth(step: Int) {
        settings.widthPercent = (widthPercent + step * 5).coerceIn(10, 100)
    }

    fun stepDuration(step: Int) {
        val increment = if (durationSeconds > 10 || (durationSeconds == 10 && step > 0)) 5 else 1
        settings.durationSeconds = (durationSeconds + step * increment).coerceIn(1, 120)
    }

    fun stepDim(step: Int) {
        val tenths = Math.round(dim * 10) + step
        settings.dim = tenths.coerceIn(0, 10) / 10f
    }

    fun stepBackground(step: Int) {
        settings.background = ColorPresets.next(ColorPresets.opaque, background, step).hex
    }

    fun stepTextColor(step: Int) {
        settings.textColor = ColorPresets.next(ColorPresets.text, textColor, step).hex
    }

    fun stepAccent(step: Int) {
        settings.accent = ColorPresets.next(ColorPresets.accent, accent, step).hex
    }

    fun stepPosition(step: Int) {
        val options = com.zorenkonte.tibeepost.model.Position.entries
        val index = options.indexOfFirst { it.wire == position }.coerceAtLeast(0)
        settings.position = options[((index + step) % options.size + options.size) % options.size].wire
    }

    fun toggleSound() {
        settings.sound = if (sound == "none") "default" else "none"
    }

    suspend fun pollEnvironment() {
        while (true) {
            ipAddress = NetworkAddress.current()
            overlayPermitted = SystemSettings.canDrawOverlays(appContext)
            serverReachable = withContext(Dispatchers.IO) { pingLocalServer() }
            delay(3_000)
        }
    }

    private fun readPreferences() {
        token = settings.token
        autostart = settings.autostart
        widthPercent = settings.widthPercent
        durationSeconds = settings.durationSeconds
        dim = settings.dim
        background = settings.background
        textColor = settings.textColor
        accent = settings.accent
        position = settings.position
        sound = settings.sound
    }

    private fun pingLocalServer(): Boolean = try {
        val connection = URL("http://127.0.0.1:$port/health").openConnection() as HttpURLConnection
        connection.connectTimeout = 1_000
        connection.readTimeout = 1_000
        val ok = connection.responseCode == 200
        connection.disconnect()
        ok
    } catch (_: Exception) {
        false
    }
}

@Composable
fun rememberSettingsState(): SettingsState {
    val context = LocalContext.current
    val state = remember { SettingsState(context) }
    DisposableEffect(state) {
        state.observePreferences()
        onDispose { state.stopObservingPreferences() }
    }
    LaunchedEffect(state) { state.pollEnvironment() }
    return state
}
