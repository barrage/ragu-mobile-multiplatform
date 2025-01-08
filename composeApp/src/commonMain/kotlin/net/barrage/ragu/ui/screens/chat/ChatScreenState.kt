package net.barrage.ragu.ui.screens.chat

import kotlinx.collections.immutable.ImmutableList
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.model.ChatMessageItem
import org.jetbrains.compose.resources.StringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.new_chat

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
        val isLoadingMessages: Boolean = false,
    ) : ChatScreenState()

    data class Error(val message: StringResource) : ChatScreenState()
}
