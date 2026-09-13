package com.zorenkonte.tibeepost.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeUiState
import com.zorenkonte.tibeepost.ui.components.QrCode
import com.zorenkonte.tibeepost.ui.components.SectionCard

@Composable
fun ConnectionCard(state: TibeeUiState) {
    SectionCard(title = stringResource(R.string.connection_title), padding = 16.dp, spacing = 8.dp) {
        Text(
            text = "${state.ipAddress ?: stringResource(R.string.status_no_network)}:${state.port}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 30.sp),
            maxLines = 1,
            softWrap = false,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    stringResource(R.string.connection_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    stringResource(R.string.connection_qr_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (state.ipAddress != null) QrCode(content = "${state.address}/", size = 96.dp)
        }
    }
}
