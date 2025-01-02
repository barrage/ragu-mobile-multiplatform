package net.barrage.ragu.ui.components.chat

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.FocusManager

data class ChatInputState(
    val onInputTextChange: (String) -> Unit,
    val onSendMessage: () -> Unit,
    val onStopReceivingMessage: () -> Unit,
    val inputText: String,
    val isEnabled: Boolean,
    val isReceivingMessage: Boolean,
    val focusManager: FocusManager,
    val chatInteractionSource: MutableInteractionSource,
)
