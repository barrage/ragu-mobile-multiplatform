package net.barrage.ragu.ui.screens.chat

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
    var lastSuccessScreenState: ChatScreenState.Success? = null

    private var tempChatTitle: String = ""

    /**
     * Updates the chat screen state using the provided update function.
     *
     * @param update A function that takes the current ChatScreenState and returns an updated ChatScreenState
     */
    fun updateChatScreenState(update: (ChatScreenState) -> ChatScreenState) {
        _chatScreenState.value = update(chatScreenState.value)
        if (_chatScreenState.value is ChatScreenState.Success) {
            lastSuccessScreenState = chatScreenState.value as ChatScreenState.Success
        }
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
                    val updatedMessages = currentState.messages.toMutableList()
                    updatedMessages.add(0, message)
                    currentState.copy(
                        messages = updatedMessages.toImmutableList()
                    )
                }

                else -> currentState
            }
        }
    }

    /**
     * Updates the first message in the chat screen state.
     *
     * @param message The new content to append to the last message
     */
    fun updateLastMessage(message: String) {
        updateChatScreenState { currentState ->
            if (currentState is ChatScreenState.Success && currentState.messages.isNotEmpty()) {
                val lastMessage = currentState.messages.first()
                val updatedMessages = currentState.messages.toMutableList()

                if (lastMessage.senderType == SenderType.ASSISTANT) {
                    updatedMessages[0] =
                        lastMessage.copy(content = lastMessage.content + message)
                } else {
                    updatedMessages.add(
                        0,
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
    fun clearChat(): ChatScreenState.Success {
        val tempChatScreenState =
            (lastSuccessScreenState ?: chatScreenState.value) as ChatScreenState.Success
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    updateAgent(tempChatScreenState.agents.firstOrNull())
                    currentState.copy(
                        messages = persistentListOf(),
                        chatTitleRes = Res.string.new_chat,
                        chatTitle = null,
                        isEditingTitle = false,
                        isReceivingMessage = false,
                        inputText = "",
                        currentAgent = currentState.agents.firstOrNull(),
                    )
                }

                else -> {
                    updateAgent(tempChatScreenState.agents.firstOrNull())
                    tempChatScreenState.copy(
                        messages = persistentListOf(),
                        chatTitleRes = Res.string.new_chat,
                        chatTitle = null,
                        isEditingTitle = false,
                        isReceivingMessage = false,
                        inputText = "",
                        currentAgent = tempChatScreenState.agents.firstOrNull(),
                    )
                }
            }
        }
        return chatScreenState.value as ChatScreenState.Success
    }

    /**
     * Updates the message evaluation in the chat screen state.
     *
     * @param message The message to update
     * @param evaluation The evaluation result (true for positive, false for negative)
     */
    fun updateMessageEvaluation(message: ChatMessageItem, evaluation: Boolean?) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    val updatedMessages = currentState.messages.map {
                        if (it.id == message.id) {
                            it.copy(evaluation = evaluation)
                        } else {
                            it
                        }
                    }
                    currentState.copy(messages = updatedMessages.toImmutableList())
                }

                else -> currentState
            }
        }
    }

    /**
     * Updates the current agent in the chat screen state.
     *
     * @param agent The new current agent
     */
    fun updateAgent(agent: Agent?) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(currentAgent = agent)
                else -> currentState
            }
        }
    }
}