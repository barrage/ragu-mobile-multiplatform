package net.barrage.chatwhitelabel.ui.screens.chat

import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.new_chat
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem
import org.jetbrains.compose.resources.StringResource

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
        val chatTitleRes: StringResource = Res.string.new_chat,
        val chatTitle: String? = null,
        val isAgentActive: Boolean,
    ) : ChatScreenState()

    data class Error(val message: StringResource) : ChatScreenState()
}
