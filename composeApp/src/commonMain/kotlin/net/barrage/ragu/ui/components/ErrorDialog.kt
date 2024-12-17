package net.barrage.ragu.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms

@Composable
fun ErrorDialog(state: ErrorDialogState, modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = state.onDismissRequest,
        title = {
            Text(
                state.title,
                style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms(),
                color = MaterialTheme.colorScheme.error,
            )
        },
        text = {
            Text(
                state.description,
                style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        confirmButton = state.confirmButton,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.fillMaxWidth(),
    )
}
