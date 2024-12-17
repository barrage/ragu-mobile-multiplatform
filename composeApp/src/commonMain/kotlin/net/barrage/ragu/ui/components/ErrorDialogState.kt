package net.barrage.ragu.ui.components

import androidx.compose.runtime.Composable

data class ErrorDialogState(
    val title: String,
    val description: String,
    val onDismissRequest: () -> Unit,
    val confirmButton: @Composable () -> Unit,
)
