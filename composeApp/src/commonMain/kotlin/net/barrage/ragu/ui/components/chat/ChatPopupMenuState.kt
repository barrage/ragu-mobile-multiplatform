package net.barrage.ragu.ui.components.chat

data class ChatPopupMenuState(
    val visible: Boolean,
    val onDismiss: () -> Unit,
    val menuItems: List<PopupMenuItemState>,
)
