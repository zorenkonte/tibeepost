package com.zorenkonte.tibeepost.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zorenkonte.tibeepost.R
import com.zorenkonte.tibeepost.ui.theme.tvFocusFrame

@Composable
fun SetupScaffold(
    stepNumber: Int?,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 64.dp, vertical = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (stepNumber != null) {
            Text(
                stringResource(R.string.setup_step, stepNumber),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(title, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
        content()
    }
    }
}

@Composable
fun PrimaryStepButton(text: String, onClick: () -> Unit, focus: FocusRequester? = null) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .then(if (focus != null) Modifier.focusRequester(focus) else Modifier)
            .tvFocusFrame(MaterialTheme.shapes.extraLarge),
    ) {
        Text(text)
    }
}

@Composable
fun SecondaryStepButton(text: String, onClick: () -> Unit, focus: FocusRequester? = null) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .then(if (focus != null) Modifier.focusRequester(focus) else Modifier)
            .tvFocusFrame(MaterialTheme.shapes.extraLarge),
    ) {
        Text(text)
    }
}

@Composable
fun ButtonBar(content: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { content() }
}

@Composable
fun UnusableWarning(packageName: String?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                stringResource(R.string.setup_unusable),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            if (packageName != null) {
                Text(
                    "adb shell appops set $packageName SYSTEM_ALERT_WINDOW allow",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}
