package com.zorenkonte.tibeepost.ui.permission

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object OverlaySettingsLauncher {
    enum class Target { OVERLAY_PAGE_FOR_APP, OVERLAY_PAGE, APP_INFO, ALL_APPS, SETTINGS }

    fun open(context: Context, packageName: String): Target? {
        val packageUri = Uri.parse("package:$packageName")
        val attempts = listOf(
            Target.OVERLAY_PAGE_FOR_APP to Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, packageUri),
            Target.OVERLAY_PAGE to Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
            Target.APP_INFO to Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri),
            Target.ALL_APPS to Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS),
            Target.SETTINGS to Intent(Settings.ACTION_SETTINGS),
        )
        for ((target, intent) in attempts) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                return target
            } catch (_: ActivityNotFoundException) {
            } catch (_: SecurityException) {
            }
        }
        return null
    }
}
