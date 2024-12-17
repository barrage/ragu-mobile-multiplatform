package net.barrage.ragu.ui.components.chat

import androidx.compose.ui.graphics.vector.ImageVector

data class PopupMenuItemState(val icon: ImageVector, val text: String, val onClick: () -> Unit)
