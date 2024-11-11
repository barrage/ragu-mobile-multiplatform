package net.barrage.chatwhitelabel.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

@Composable
fun NoInternetAlert(modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = { /* No-op, as we don't want to dismiss this alert */ },
        title = {
            Text(
                "NO INTERNET CONNECTION",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
            )
        },
        text = {
            Text(
                "No internet connection detected. Please check your connection " +
                    "and try again to continue using the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        confirmButton = { /* No buttons needed */ },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.fillMaxWidth(),
    )
}
