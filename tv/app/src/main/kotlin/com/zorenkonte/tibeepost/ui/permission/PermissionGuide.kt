package com.zorenkonte.tibeepost.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.SectionCard
import com.zorenkonte.tibeepost.ui.theme.tvFocusFrame

@Composable
fun PermissionGuide(
    state: TibeeUiState,
    actions: TibeeActions,
    primaryFocus: FocusRequester,
    modifier: Modifier = Modifier,
    footer: @Composable () -> Unit = {},
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (state.overlayPermitted) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.setup_permission_granted_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(stringResource(R.string.setup_permission_granted_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        } else {
            Text(stringResource(R.string.setup_permission_body), style = MaterialTheme.typography.bodyLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionCard(title = stringResource(R.string.guide_where_title), modifier = Modifier.weight(1f)) {
                    GuideStep(1, stringResource(R.string.guide_step_1))
                    GuideStep(2, stringResource(R.string.guide_step_2))
                    GuideStep(3, stringResource(R.string.guide_step_3))
                    GuideStep(4, stringResource(R.string.guide_step_4))
                    GuideStep(5, stringResource(R.string.guide_step_5))
                }
                SectionCard(title = stringResource(R.string.guide_adb_title), modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.guide_adb_body), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "adb shell appops set ${state.packageName} SYSTEM_ALERT_WINDOW allow",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(stringResource(R.string.setup_permission_recheck), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = actions::openOverlaySettings,
                    modifier = Modifier.focusRequester(primaryFocus).tvFocusFrame(MaterialTheme.shapes.extraLarge),
                ) {
                    Text(stringResource(R.string.guide_open_button))
                }
                footer()
            }
            state.lastOpenedSettings?.let { target ->
                Text(
                    text = stringResource(openedLabel(target)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun GuideStep(number: Int, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("$number.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun openedLabel(target: String): Int = when (target) {
    OverlaySettingsLauncher.Target.OVERLAY_PAGE_FOR_APP.name -> R.string.guide_opened_overlay_app
    OverlaySettingsLauncher.Target.OVERLAY_PAGE.name -> R.string.guide_opened_overlay_list
    OverlaySettingsLauncher.Target.APP_INFO.name -> R.string.guide_opened_app_info
    OverlaySettingsLauncher.Target.ALL_APPS.name -> R.string.guide_opened_all_apps
    OverlaySettingsLauncher.Target.SETTINGS.name -> R.string.guide_opened_settings
    else -> R.string.guide_opened_none
}

@Composable
fun SecondaryGuideButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick, modifier = Modifier.tvFocusFrame(MaterialTheme.shapes.extraLarge)) { Text(text) }
}
