package com.zorenkonte.tibeepost.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zorenkonte.tibeepost.R

@Composable
fun TokenRow(state: SettingsState) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(R.string.row_token), style = MaterialTheme.typography.titleLarge)
        Text(
            text = state.token.ifEmpty { stringResource(R.string.row_token_off) },
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = state::generateToken) { Text(stringResource(R.string.token_generate)) }
            if (state.token.isNotEmpty()) {
                Button(onClick = state::clearToken) { Text(stringResource(R.string.token_clear)) }
            }
        }
    }
}
