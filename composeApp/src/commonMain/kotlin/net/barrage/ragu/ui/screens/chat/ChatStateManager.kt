package net.barrage.ragu.ui.screens.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.barrage.ragu.data.remote.dto.history.SenderType
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.model.ChatMessageItem
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.new_chat

/**
 * Manages the state of the chat screen.
 */
class ChatStateManager {

    private val _chatScreenState = MutableStateFlow<ChatScreenState>(ChatScreenState.Idle)
    val chatScreenState: StateFlow<ChatScreenState> = _chatScreenState.asStateFlow()

    var selectedAgent: MutableState<Agent?> = mutableStateOf(null)
        private set

    private var tempChatTitle: String = ""

    /**
     * Updates the chat screen state using the provided update function.
     *
     * @param update A function that takes the current ChatScreenState and returns an updated ChatScreenState
     */
    fun updateChatScreenState(update: (ChatScreenState) -> ChatScreenState) {
        _chatScreenState.value = update(chatScreenState.value)
    }

    /**
     * Updates the input text in the chat screen state.
     *
     * @param text The new input text
     */
    fun updateInputText(text: String) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(inputText = text)
                else -> currentState
            }
        }
    }

    /**
     * Adds a new message to the chat screen state.
     *
     * @param messageContent The content of the message
     * @param senderType The type of sender (USER, ASSISTANT, or ERROR)
     */
    fun addMessage(messageContent: String, senderType: SenderType) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    val message = ChatMessageItem(content = messageContent, senderType = senderType)
                    currentState.copy(
                        messages = (currentState.messages + message).toImmutableList()
                    )
                }

                else -> currentState
            }
        }
    }

    /**
     * Updates the last message in the chat screen state.
     *
     * @param message The new content to append to the last message
     */
    fun updateLastMessage(message: String) {
        updateChatScreenState { currentState ->
            if (currentState is ChatScreenState.Success && currentState.messages.isNotEmpty()) {
                val lastMessage = currentState.messages.last()
                val updatedMessages = currentState.messages.toMutableList()

                if (lastMessage.senderType == SenderType.ASSISTANT) {
                    updatedMessages[updatedMessages.lastIndex] =
                        lastMessage.copy(content = lastMessage.content + message)
                } else {
                    updatedMessages.add(
                        ChatMessageItem(content = message, senderType = SenderType.ASSISTANT)
                    )
                }

                currentState.copy(messages = updatedMessages.toImmutableList())
            } else {
                currentState
            }
        }
    }

    /**
     * Sets the chat title.
     *
     * @param title The new chat title
     * @param chatId The ID of the chat (optional)
     */
    fun setChatTitle(title: String, chatId: String?) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(chatTitle = title)
                else -> currentState
            }
        }
    }

    /**
     * Sets whether the chat title is being edited.
     *
     * @param editing True if the title is being edited, false otherwise
     */
    fun setEditingTitle(editing: Boolean) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    if (editing && currentState.chatTitle != null) {
                        tempChatTitle = currentState.chatTitle
                    }
                    currentState.copy(isEditingTitle = editing)
                }

                else -> currentState
            }
        }
    }

    /**
     * Cancels the title edit operation.
     */
    fun cancelTitleEdit() {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    currentState.copy(chatTitle = tempChatTitle, isEditingTitle = false)
                }

                else -> currentState
            }
        }
        tempChatTitle = "" // Clear temporary title
    }

    /**
     * Clears the current chat.
     */
    fun clearChat(tempChatScreenState: ChatScreenState.Success? = null) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success ->
                    currentState.copy(
                        messages = persistentListOf(),
                        chatTitleRes = Res.string.new_chat,
                        chatTitle = null,
                        isEditingTitle = false,
                        isReceivingMessage = false,
                        inputText = "",
                        isAgentActive = true,
                    )

                else -> tempChatScreenState?.copy(
                    messages = persistentListOf(),
                    chatTitleRes = Res.string.new_chat,
                    chatTitle = null,
                    isEditingTitle = false,
                    isReceivingMessage = false,
                    inputText = "",
                    isAgentActive = true,
                ) ?: ChatScreenState.Idle
            }
        }
    }

    /**
     * Sets the current agent for the chat.
     *
     * @param agent The agent to set
     */
    fun setAgent(agent: Agent) {
        selectedAgent.value = agent
    }
}