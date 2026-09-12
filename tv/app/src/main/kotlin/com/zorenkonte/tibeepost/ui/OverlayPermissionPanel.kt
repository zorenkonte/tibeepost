package com.zorenkonte.tibeepost.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R

@Composable
fun OverlayPermissionPanel(packageName: String) {
    val context = LocalContext.current
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
    val canOpenSystemDialog = intent.resolveActivity(context.packageManager) != null

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF3F1D1D), RoundedCornerShape(12.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.overlay_missing_title), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.overlay_missing_body), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "adb shell appops set $packageName SYSTEM_ALERT_WINDOW allow",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
            color = Color(0xFFFDE68A),
        )
        if (canOpenSystemDialog) {
            Button(onClick = { context.startActivity(intent) }) {
                Text(stringResource(R.string.overlay_open_settings))
            }
        }
    }
}
