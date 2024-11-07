package net.barrage.chatwhitelabel.ui.components.chat

data class ChatTitleState(
    val title: String,
    val menuVisible: Boolean,
    val onThreeDotsClick: () -> Unit,
    val onEditTitleClick: () -> Unit,
    val onDeleteChatClick: () -> Unit,
    val onDismiss: () -> Unit,
)
