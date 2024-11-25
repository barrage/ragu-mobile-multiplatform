package net.barrage.chatwhitelabel.ui.screens.chat

import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem

sealed class ChatScreenState {
    data object Idle : ChatScreenState()

    data object Loading : ChatScreenState()

    data class Success(
        val agents: ImmutableList<Agent>,
        val messages: ImmutableList<ChatMessageItem>,
        val inputText: String = "",
        val isSendEnabled: Boolean = false,
        val isReceivingMessage: Boolean = false,
        val isEditingTitle: Boolean = false,
        val chatTitle: String = "New chat",
        val isAgentActive: Boolean,
    ) : ChatScreenState()

    data class Error(val message: String) : ChatScreenState()
}
