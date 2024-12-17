package net.barrage.ragu.ui.components.chat

import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.RevealState

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
    val onTitleChangeDismiss: () -> Unit,
    val revealCanvasState: RevealCanvasState,
    val revealState: RevealState,
)
