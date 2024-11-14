package net.barrage.chatwhitelabel.ui.components.chat

data class ChatTitleState(
    val title: String,
    val isMenuVisible: Boolean,
    val isEditingTitle: Boolean,
    val onThreeDotsClick: () -> Unit,
    val onEditTitleClick: () -> Unit,
    val onDeleteChatClick: () -> Unit,
    val onDismiss: () -> Unit,
    val onTitleChange: (String) -> Unit,
    val onTitleChangeConfirmation: () -> Unit,
)
