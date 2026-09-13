package com.zorenkonte.tibeepost.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.TibeeActions
import com.zorenkonte.tibeepost.ui.TibeeUiState

@Composable
fun PermissionGuideScreen(state: TibeeUiState, actions: TibeeActions, onBack: () -> Unit) {
    val primaryFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { primaryFocus.requestFocus() }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            stringResource(R.string.setup_permission_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        PermissionGuide(
            state = state,
            actions = actions,
            primaryFocus = primaryFocus,
            footer = { SecondaryGuideButton(stringResource(R.string.guide_back), onBack) },
        )
        if (state.overlayPermitted) {
            SecondaryGuideButton(stringResource(R.string.guide_done), onBack)
        }
    }
    }
}
