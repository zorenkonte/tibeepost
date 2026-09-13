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
import com.zorenkonte.tibeepost.sound.ChimePlayer
import com.zorenkonte.tibeepost.ui.permission.OverlaySettingsLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SettingsState(context: Context) : TibeeActions {
    private val appContext = context.applicationContext
    private val settings = Settings(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var chime: ChimePlayer? = null

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
    var lastOpenedSettings by mutableStateOf<OverlaySettingsLauncher.Target?>(null)
        private set

    val port: Int = ServerConfig.PORT
    val packageName: String = appContext.packageName

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> readPreferences() }

    fun start() = settings.observe(preferenceListener)

    fun dispose() {
        settings.stopObserving(preferenceListener)
        scope.cancel()
        chime?.release()
        chime = null
    }

    fun snapshot() = TibeeUiState(
        ipAddress = ipAddress,
        port = port,
        packageName = packageName,
        serverReachable = serverReachable,
        overlayPermitted = overlayPermitted,
        token = token,
        autostart = autostart,
        widthPercent = widthPercent,
        durationSeconds = durationSeconds,
        dim = dim,
        background = background,
        textColor = textColor,
        accent = accent,
        position = position,
        sound = sound,
        testResult = testResult,
        lastOpenedSettings = lastOpenedSettings?.name,
    )

    override fun sendTest() {
        scope.launch { testResult = TestNotificationSender.send(token) }
    }

    override fun previewChime() {
        (chime ?: ChimePlayer(appContext).also { chime = it }).play()
    }

    override fun generateToken() {
        settings.token = TokenGenerator.generate()
    }

    override fun clearToken() {
        settings.token = ""
    }

    override fun toggleAutostart(enabled: Boolean) {
        settings.autostart = enabled
    }

    override fun changeWidth(percent: Int) {
        settings.widthPercent = percent.coerceIn(10, 100)
    }

    override fun changeDuration(seconds: Int) {
        settings.durationSeconds = seconds.coerceIn(1, 120)
    }

    override fun changeDim(opacity: Float) {
        settings.dim = opacity.coerceIn(0f, 1f)
    }

    override fun chooseBackground(hex: String) {
        settings.background = hex
    }

    override fun chooseTextColor(hex: String) {
        settings.textColor = hex
    }

    override fun chooseAccent(hex: String) {
        settings.accent = hex
    }

    override fun choosePosition(wire: String) {
        settings.position = wire
    }

    override fun chooseSound(wire: String) {
        settings.sound = wire
    }

    override fun openOverlaySettings() {
        lastOpenedSettings = OverlaySettingsLauncher.open(appContext, packageName)
    }

    override fun completeOnboarding() {
        settings.onboardingDone = true
    }

    override fun resetOnboarding() {
        settings.onboardingDone = false
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
        state.start()
        onDispose { state.dispose() }
    }
    LaunchedEffect(state) { state.pollEnvironment() }
    return state
}
