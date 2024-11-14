package net.barrage.chatwhitelabel.ui.components.chat

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.FocusManager

data class ChatInputState(
    val inputText: String,
    val onInputTextChange: (String) -> Unit,
    val onSendMessage: () -> Unit,
    val onStopReceivingMessage: () -> Unit,
    val isEnabled: Boolean,
    val isReceivingMessage: Boolean,
    val focusManager: FocusManager,
    val chatInteractionSource: MutableInteractionSource,
)
