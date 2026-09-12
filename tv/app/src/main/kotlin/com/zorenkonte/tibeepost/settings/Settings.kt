package com.zorenkonte.tibeepost.settings

import android.content.Context
import android.content.SharedPreferences

class Settings(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(SettingsKeys.FILE, Context.MODE_PRIVATE)

    var token: String
        get() = prefs.getString(SettingsKeys.TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(SettingsKeys.TOKEN, value).apply()

    var autostart: Boolean
        get() = prefs.getBoolean(SettingsKeys.AUTOSTART, true)
        set(value) = prefs.edit().putBoolean(SettingsKeys.AUTOSTART, value).apply()

    var widthPercent: Int
        get() = prefs.getInt(SettingsKeys.WIDTH_PERCENT, 60)
        set(value) = prefs.edit().putInt(SettingsKeys.WIDTH_PERCENT, value).apply()

    var durationSeconds: Int
        get() = prefs.getInt(SettingsKeys.DURATION_SECONDS, 15)
        set(value) = prefs.edit().putInt(SettingsKeys.DURATION_SECONDS, value).apply()

    var dim: Float
        get() = prefs.getFloat(SettingsKeys.DIM, 0f)
        set(value) = prefs.edit().putFloat(SettingsKeys.DIM, value).apply()

    var background: String
        get() = prefs.getString(SettingsKeys.BACKGROUND, "#FFFFFF") ?: "#FFFFFF"
        set(value) = prefs.edit().putString(SettingsKeys.BACKGROUND, value).apply()

    var textColor: String
        get() = prefs.getString(SettingsKeys.TEXT_COLOR, "#111111") ?: "#111111"
        set(value) = prefs.edit().putString(SettingsKeys.TEXT_COLOR, value).apply()

    var accent: String
        get() = prefs.getString(SettingsKeys.ACCENT, "") ?: ""
        set(value) = prefs.edit().putString(SettingsKeys.ACCENT, value).apply()

    var position: String
        get() = prefs.getString(SettingsKeys.POSITION, "center") ?: "center"
        set(value) = prefs.edit().putString(SettingsKeys.POSITION, value).apply()

    var sound: String
        get() = prefs.getString(SettingsKeys.SOUND, "default") ?: "default"
        set(value) = prefs.edit().putString(SettingsKeys.SOUND, value).apply()

    fun observe(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        prefs.registerOnSharedPreferenceChangeListener(listener)

    fun stopObserving(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
}
