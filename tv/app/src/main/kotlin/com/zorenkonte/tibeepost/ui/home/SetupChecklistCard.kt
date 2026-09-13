package com.zorenkonte.tibeepost.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.SectionCard
import com.zorenkonte.tibeepost.ui.components.Tone
import com.zorenkonte.tibeepost.ui.components.ValuePill
import com.zorenkonte.tibeepost.ui.components.color
import com.zorenkonte.tibeepost.ui.theme.tvFocusFrame

@Composable
fun SetupChecklistCard(state: TibeeUiState, onFixPermission: () -> Unit, modifier: Modifier = Modifier) {
    SectionCard(title = stringResource(R.string.checklist_title), modifier = modifier, padding = 16.dp, spacing = 6.dp) {
        ChecklistRow(
            title = stringResource(R.string.checklist_overlay),
            detail = if (state.overlayPermitted) stringResource(R.string.status_granted) else stringResource(R.string.checklist_overlay_missing),
            tone = if (state.overlayPermitted) Tone.GOOD else Tone.BAD,
            action = if (state.overlayPermitted) null else stringResource(R.string.checklist_fix),
            onClick = onFixPermission,
        )
        ChecklistRow(
            title = stringResource(R.string.checklist_server),
            detail = when (state.serverReachable) {
                true -> stringResource(R.string.checklist_server_on, state.port)
                false -> stringResource(R.string.status_not_running)
                null -> stringResource(R.string.status_checking)
            },
            tone = when (state.serverReachable) {
                true -> Tone.GOOD
                false -> Tone.BAD
                null -> Tone.NEUTRAL
            },
        )
        ChecklistRow(
            title = stringResource(R.string.checklist_network),
            detail = state.ipAddress ?: stringResource(R.string.checklist_network_missing),
            tone = if (state.ipAddress != null) Tone.GOOD else Tone.BAD,
        )
    }
}

@Composable
private fun ChecklistRow(
    title: String,
    detail: String,
    tone: Tone,
    action: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.large
    val interactive = onClick != null
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (interactive) Modifier.tvFocusFrame(shape, focusedScale = 1.01f).clickable { onClick?.invoke() } else Modifier),
        shape = shape,
        color = if (interactive) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
    ) {
        Row(
            Modifier.padding(horizontal = if (interactive) 12.dp else 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.size(26.dp).background(tone.color.copy(alpha = 0.18f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (tone == Tone.BAD) Icons.Default.Close else Icons.Default.Check,
                    contentDescription = null,
                    tint = tone.color,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (action != null) ValuePill(action, emphasized = tone == Tone.BAD)
        }
    }
}
