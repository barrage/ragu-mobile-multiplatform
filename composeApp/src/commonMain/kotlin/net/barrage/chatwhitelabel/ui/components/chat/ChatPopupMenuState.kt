package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.ui.geometry.Offset

data class ChatPopupMenuState(
    val visible: Boolean,
    val onDismiss: () -> Unit,
    val menuItems: List<PopupMenuItemState>,
    val iconPositionInRoot: Offset,
    val iconPositionInParent: Offset,
)
