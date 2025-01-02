package net.barrage.ragu.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import net.barrage.ragu.data.remote.dto.history.SenderType
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.model.ChatMessageItem
import net.barrage.ragu.domain.usecase.auth.LogoutUseCase
import net.barrage.ragu.domain.usecase.chat.ChatUseCase
import net.barrage.ragu.domain.usecase.user.CurrentUserUseCase
import net.barrage.ragu.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.ragu.ui.screens.history.HistoryScreenStates
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.ragu.utils.debugLog
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.failed_to_load_agents

/**
 * ViewModel for managing the chat screen state and operations.
 *
 * This class handles the business logic for the chat functionality, including
 * managing the chat state, WebSocket connections, and user interactions.
 *
 * @property webSocketTokenUseCase Use case for obtaining WebSocket tokens
 * @property chatUseCase Use case for chat-related operations
 * @property currentUserUseCase Use case for getting the current user
 * @property logoutUseCase Use case for logging out
 */
class ChatViewModel(
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    private val chatUseCase: ChatUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val chatStateManager = ChatStateManager()
    val chatHistoryManager = ChatHistoryManager(chatUseCase)
    val webSocketManager = WebSocketManager(webSocketTokenUseCase)

    val chatScreenState: StateFlow<ChatScreenState> = chatStateManager.chatScreenState
    val historyViewState = chatHistoryManager.historyViewState
    val selectedAgent = chatStateManager.selectedAgent

    private val _currentUserViewState =
        MutableStateFlow<HistoryScreenStates<ProfileViewState>>(HistoryScreenStates.Idle)
    val currentUserViewState: StateFlow<HistoryScreenStates<ProfileViewState>> =
        _currentUserViewState.asStateFlow()

    /**
     * Sets the receiving message state in the chat screen.
     *
     * @param isReceiving Boolean indicating whether a message is being received
     */
    fun setReceivingMessage(isReceiving: Boolean) {
        chatStateManager.updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(isReceivingMessage = isReceiving)
                else -> currentState
            }
        }
    }

    /**
     * Sets the send enabled state in the chat screen.
     *
     * @param enabled Boolean indicating whether sending is enabled
     */
    fun setSendEnabled(enabled: Boolean) {
        viewModelScope.launch {
            var currentState = chatStateManager.chatScreenState.value
            while (currentState !is ChatScreenState.Success) {
                delay(100) // Wait for 100ms before checking again
                currentState = chatStateManager.chatScreenState.value
            }
            chatStateManager.updateChatScreenState { state ->
                (state as ChatScreenState.Success).copy(isSendEnabled = enabled)
            }
        }
    }

    /**
     * Loads all initial data for the chat screen, including agents and user information.
     */
    fun loadAllData() {
        viewModelScope.launch {
            chatStateManager.updateChatScreenState { ChatScreenState.Loading }
            launch { chatHistoryManager.updateHistory() }
            launch { updateCurrentUser() }

            chatUseCase.getAgents().collectLatest { agentsResponse ->
                when (agentsResponse) {
                    is Response.Loading -> {
                        chatStateManager.updateChatScreenState { ChatScreenState.Loading }
                    }

                    is Response.Success -> {
                        chatStateManager.updateChatScreenState { currentState ->
                            when (currentState) {
                                is ChatScreenState.Success -> {
                                    val firstAgent = agentsResponse.data.firstOrNull()
                                    if (firstAgent != null) {
                                        chatStateManager.setAgent(firstAgent)
                                    }
                                    currentState.copy(
                                        agents = agentsResponse.data.toImmutableList(),
                                        isAgentActive = true
                                    )
                                }

                                else -> {
                                    val firstAgent = agentsResponse.data.firstOrNull()
                                    if (firstAgent != null) {
                                        chatStateManager.setAgent(firstAgent)
                                    }
                                    ChatScreenState.Success(
                                        agents = agentsResponse.data.toImmutableList(),
                                        messages = persistentListOf(),
                                        isAgentActive = true
                                    )
                                }
                            }
                        }
                    }

                    is Response.Failure -> {
                        chatStateManager.updateChatScreenState { ChatScreenState.Error(Res.string.failed_to_load_agents) }
                    }

                    is Response.Unauthorized -> {
                        // Handle unauthorized response, perhaps by redirecting to login
                    }
                }
            }
        }
    }

    /**
     * Updates the input text in the chat state.
     *
     * @param text The new input text
     */
    fun updateInputText(text: String) {
        chatStateManager.updateInputText(text)
    }

    /**
     * Adds a new message to the chat state.
     *
     * @param messageContent The content of the message
     * @param senderType The type of the sender (USER or ASSISTANT)
     */
    fun addMessage(messageContent: String, senderType: SenderType) {
        chatStateManager.addMessage(messageContent, senderType)
    }

    /**
     * Sends the current input message via WebSocket.
     */
    fun sendMessage() {
        val currentState = chatScreenState.value
        if (currentState is ChatScreenState.Success && currentState.inputText.isNotEmpty()) {
            addMessage(currentState.inputText, SenderType.USER)
            webSocketManager.sendMessage(currentState.inputText)
            updateInputText("")
        }
    }

    /**
     * Updates the last message in the chat state.
     *
     * @param message The new content to append to the last message
     */
    fun updateLastMessage(message: String) {
        chatStateManager.updateLastMessage(message)
    }

    /**
     * Initializes the WebSocket client.
     *
     * @param callback The callback to handle received messages
     * @param scope The coroutine scope to use for the WebSocket client
     */
    fun initializeWebSocketClient(callback: ReceiveMessageCallback, scope: CoroutineScope) {
        viewModelScope.launch {
            webSocketManager.initializeWebSocketClient(callback, scope, selectedAgent)
        }
    }

    /**
     * Sets the chat title.
     *
     * @param title The new chat title
     * @param chatId The ID of the chat (optional)
     */
    fun setChatTitle(title: String, chatId: String? = null) {
        chatStateManager.setChatTitle(title, chatId)
    }

    /**
     * Sets the editing title state.
     *
     * @param editing Boolean indicating whether the title is being edited
     */
    fun setEditingTitle(editing: Boolean) {
        chatStateManager.setEditingTitle(editing)
    }

    /**
     * Updates the chat title on the server.
     */
    fun updateTitle() {
        viewModelScope.launch {
            val currentState = chatScreenState.value
            if (currentState is ChatScreenState.Success &&
                !webSocketManager.webSocketChatClient?.currentChatId?.value.isNullOrEmpty() &&
                currentState.chatTitle?.isNotEmpty() == true
            ) {
                currentState.chatTitle.let { chatTitle ->
                    val response = chatUseCase.updateChatTitle(
                        webSocketManager.webSocketChatClient?.currentChatId?.value!!,
                        chatTitle,
                    )
                    if (response is Response.Success) {
                        chatStateManager.setEditingTitle(false)
                    } else {
                        // Handle error
                    }
                }
            }
        }
    }

    /**
     * Cancels the title edit operation.
     */
    fun cancelTitleEdit() {
        chatStateManager.cancelTitleEdit()
    }

    /**
     * Deletes the current chat.
     */
    fun deleteChat() {
        viewModelScope.launch {
            val tempChatScreenState = chatScreenState.value
            if (!webSocketManager.webSocketChatClient?.currentChatId?.value.isNullOrEmpty()) {
                chatStateManager.updateChatScreenState { ChatScreenState.Loading }
                webSocketManager.stopMessageStream()
                val response =
                    chatUseCase.deleteChat(webSocketManager.webSocketChatClient?.currentChatId?.value!!)
                if (response is Response.Success) {
                    chatStateManager.clearChat(if (tempChatScreenState is ChatScreenState.Success) tempChatScreenState else null)
                    webSocketManager.setChatId(null)
                } else {
                    chatStateManager.updateChatScreenState { tempChatScreenState }
                }
            }
        }
    }

    /**
     * Sets the current agent for the chat.
     *
     * @param agent The agent to set
     */
    fun setAgent(agent: Agent) {
        chatStateManager.setAgent(agent)
    }

    /**
     * Starts a new chat session.
     */
    fun newChat() {
        val currentState = chatScreenState.value
        if (currentState is ChatScreenState.Success && currentState.isReceivingMessage) {
            webSocketManager.stopMessageStream()
        }
        chatStateManager.clearChat()
        webSocketManager.setChatId(null)
    }

    /**
     * Retrieves a chat by its ID.
     *
     * @param id The ID of the chat to retrieve
     * @param title The title of the chat
     */
    fun getChatById(id: String, title: String) {
        viewModelScope.launch {
            if (webSocketManager.webSocketChatClient?.currentChatId?.value == id) {
                return@launch
            }
            val tempChatScreenState = chatScreenState.value
            if (tempChatScreenState is ChatScreenState.Success &&
                tempChatScreenState.isReceivingMessage
            ) {
                webSocketManager.stopMessageStream()
            }
            chatStateManager.updateChatScreenState { ChatScreenState.Loading }

            chatUseCase.getChatMessagesById(id)
                .combine(chatUseCase.getChatById(id)) { messagesResponse, chatResponse ->
                    Pair(messagesResponse, chatResponse)
                }
                .collect { (chatMessagesResponse, chatResponse) ->
                    when {
                        chatMessagesResponse is Response.Success && chatResponse is Response.Success -> {
                            webSocketManager.setChatId(id)
                            chatHistoryManager.updateHistory(currentChatId = webSocketManager.webSocketChatClient?.currentChatId?.value)
                            chatStateManager.updateChatScreenState {
                                when (tempChatScreenState) {
                                    is ChatScreenState.Success ->
                                        tempChatScreenState.copy(
                                            messages = chatMessagesResponse.data.toImmutableList(),
                                            chatTitle = title,
                                            isEditingTitle = false,
                                            isReceivingMessage = false,
                                            inputText = "",
                                            isAgentActive = chatResponse.data.agent.active,
                                        )

                                    else ->
                                        ChatScreenState.Success(
                                            agents = persistentListOf(),
                                            messages = chatMessagesResponse.data.toImmutableList(),
                                            chatTitle = title,
                                            isEditingTitle = false,
                                            isReceivingMessage = false,
                                            inputText = "",
                                            isAgentActive = true,
                                        )
                                }
                            }
                        }

                        else -> {
                            // Handle error
                        }
                    }
                }
        }
    }

    /**
     * Logs out the current user.
     *
     * @param onLogoutSuccess Callback to be invoked on successful logout
     */
    fun logout(onLogoutSuccess: () -> Unit) {
        val tempChatScreenState = chatScreenState.value
        viewModelScope.launch {
            logoutUseCase().collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        onLogoutSuccess()
                    }

                    is Response.Failure -> {
                        // Handle logout failure
                        chatStateManager.updateChatScreenState { tempChatScreenState }
                    }

                    is Response.Loading -> {
                        chatStateManager.updateChatScreenState { ChatScreenState.Loading }
                    }

                    else -> {
                        // Handle other cases
                    }
                }
            }
        }
    }

    fun clearViewModel() {
        chatStateManager.clearChat()
        webSocketManager.disconnect()
        _currentUserViewState.value = HistoryScreenStates.Idle
    }

    /**
     * Loads more chat history.
     */
    fun loadMoreHistory() {
        viewModelScope.launch {
            chatHistoryManager.loadMoreHistory(webSocketManager.webSocketChatClient?.currentChatId?.value)
        }
    }

    /**
     * Updates the current user information.
     */
    private fun updateCurrentUser() {
        viewModelScope.launch {
            currentUserUseCase().collect { response ->
                _currentUserViewState.value = when (response) {
                    is Response.Success -> {
                        HistoryScreenStates.Success(response.data.toViewState())
                    }

                    is Response.Failure -> HistoryScreenStates.Error
                    is Response.Loading -> HistoryScreenStates.Loading
                    is Response.Unauthorized -> HistoryScreenStates.Error
                }
            }
        }
    }

    /**
     * Evaluates a message.
     *
     * @param message The message to evaluate
     * @param evaluation The evaluation result (true for positive, false for negative)
     */
    fun evaluateMessage(message: ChatMessageItem, evaluation: Boolean) {
        viewModelScope.launch {
            if (!message.chatId.isNullOrEmpty() && !message.id.isNullOrEmpty()) {
                val result = chatUseCase.evaluateMessage(message.chatId, message.id, evaluation)
                debugLog("Evaluation result: $result")
            }
        }
    }
}