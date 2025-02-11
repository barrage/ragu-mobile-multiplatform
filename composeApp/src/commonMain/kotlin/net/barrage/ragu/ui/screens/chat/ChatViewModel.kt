package net.barrage.ragu.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
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
import net.barrage.ragu.domain.usecase.user.DeleteProfileAvatarUseCase
import net.barrage.ragu.domain.usecase.user.UpdateProfileAvatarUseCase
import net.barrage.ragu.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.ragu.encodeToByteArray
import net.barrage.ragu.ui.screens.history.HistoryScreenStates
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.ragu.utils.SnackbarHelper
import net.barrage.ragu.utils.debugLog
import net.barrage.ragu.utils.debugLogError
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.failed_to_load_agents
import ragumultiplatform.composeapp.generated.resources.failed_to_load_chat_messages
import ragumultiplatform.composeapp.generated.resources.message_evaluated

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
    private val updateProfileAvatarUseCase: UpdateProfileAvatarUseCase,
    private val deleteProfileAvatarUseCase: DeleteProfileAvatarUseCase
) : ViewModel() {

    val chatStateManager = ChatStateManager()
    val chatHistoryManager = ChatHistoryManager(chatUseCase)
    val webSocketManager = WebSocketManager(webSocketTokenUseCase)

    val chatScreenState: StateFlow<ChatScreenState> = chatStateManager.chatScreenState
    val historyViewState = chatHistoryManager.historyViewState
    private val _selectedAgent = MutableStateFlow<Agent?>(null)
    val selectedAgent = _selectedAgent.asStateFlow()

    private val _currentUserViewState =
        MutableStateFlow<HistoryScreenStates<ProfileViewState>>(HistoryScreenStates.Idle)
    val currentUserViewState: StateFlow<HistoryScreenStates<ProfileViewState>> =
        _currentUserViewState.asStateFlow()

    private val _deleteAvatarVisible = MutableStateFlow(false)
    val deleteAvatarVisible = _deleteAvatarVisible.asStateFlow()

    private var currentChatMessagesPage = 1
    private var isLastChatMessagesPage = false
    private var isNewChat = true
    private val chatMessagesPageSize = 21

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
            var tempChatScreenState =
                chatStateManager.lastSuccessScreenState ?: chatStateManager.chatScreenState.value
            while (tempChatScreenState !is ChatScreenState.Success) {
                delay(100) // Wait for 100ms before checking again
                tempChatScreenState = chatStateManager.lastSuccessScreenState
                    ?: chatStateManager.chatScreenState.value
            }
            chatStateManager.updateChatScreenState {
                tempChatScreenState.copy(isSendEnabled = enabled)
            }
        }
    }

    /**
     * Loads all initial data for the chat screen, including agents and user information.
     */
    suspend fun loadAllData() = coroutineScope {
        chatStateManager.updateChatScreenState { ChatScreenState.Loading }
        val tempChatScreenState = chatStateManager.lastSuccessScreenState ?: chatScreenState.value
        launch { chatHistoryManager.updateHistory(currentChatId = webSocketManager.webSocketChatClient?.currentChatId?.value) }
        launch { updateCurrentUser() }

        chatStateManager.updateChatScreenState { ChatScreenState.Loading }
        chatUseCase.getAgents(withAvatar = true).collectLatest { agentsResponse ->
            when (agentsResponse) {
                is Response.Loading -> {
                    chatStateManager.updateChatScreenState { ChatScreenState.Loading }
                }

                is Response.Success -> {
                    chatStateManager.updateChatScreenState {
                        when (tempChatScreenState) {
                            is ChatScreenState.Success -> {
                                val firstAgent = agentsResponse.data.firstOrNull()
                                val agent =
                                    if (tempChatScreenState.messages.isNotEmpty()) agentsResponse.data.find { it.id == tempChatScreenState.currentAgent?.id }
                                        ?: tempChatScreenState.currentAgent?.copy(active = false) else firstAgent
                                if (webSocketManager.webSocketChatClient?.selectedAgent?.value != agent && agent != null) {
                                    setAgent(agent)
                                }
                                tempChatScreenState.copy(
                                    agents = agentsResponse.data.toImmutableList(),
                                    currentAgent = agent,
                                )
                            }

                            else -> {
                                val agent =
                                    agentsResponse.data.find { it.id == webSocketManager.webSocketChatClient?.selectedAgent?.value?.id }
                                        ?: webSocketManager.webSocketChatClient?.selectedAgent?.value?.copy(
                                            active = false
                                        ) ?: agentsResponse.data.firstOrNull()
                                if (agent != null) {
                                    setAgent(agent)
                                }
                                ChatScreenState.Success(
                                    agents = agentsResponse.data.toImmutableList(),
                                    messages = persistentListOf(),
                                    currentAgent = agent,
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
            webSocketManager.initializeWebSocketClient(
                callback,
                scope,
                selectedAgent,
                handleChatId = {
                    val tempChatScreenState =
                        chatStateManager.lastSuccessScreenState ?: chatScreenState.value
                    if (tempChatScreenState is ChatScreenState.Success && tempChatScreenState.messages.isNotEmpty()) {
                        webSocketManager.setChatId(it, selectedAgent.value)
                    }
                })
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
        webSocketManager.webSocketChatClient?.currentChatId?.value = chatId
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
            if (currentState is ChatScreenState.Success && !webSocketManager.webSocketChatClient?.tempChatId?.value.isNullOrEmpty() && currentState.chatTitle?.isNotEmpty() == true) {
                currentState.chatTitle.let { chatTitle ->
                    val response = chatUseCase.updateChatTitle(
                        webSocketManager.webSocketChatClient?.tempChatId?.value!!,
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
            val tempChatScreenState =
                chatStateManager.lastSuccessScreenState ?: chatScreenState.value
            if (!webSocketManager.webSocketChatClient?.currentChatId?.value.isNullOrEmpty()) {
                chatStateManager.updateChatScreenState { ChatScreenState.Loading }
                webSocketManager.stopMessageStream()
                val response =
                    chatUseCase.deleteChat(webSocketManager.webSocketChatClient?.currentChatId?.value!!)
                if (response is Response.Success) {
                    chatStateManager.clearChat()
                    webSocketManager.setChatId(null, null)
                    isNewChat = true
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
        viewModelScope.launch {
            _selectedAgent.emit(agent)
            chatStateManager.updateAgent(agent)
        }
    }

    /**
     * Starts a new chat session.
     */
    fun newChat() {
        webSocketManager.setChatId(null, null)
        val currentState = chatScreenState.value
        if (currentState is ChatScreenState.Success && currentState.isReceivingMessage) {
            webSocketManager.stopMessageStream()
        }
        val clearedState = chatStateManager.clearChat()
        clearedState.agents.firstOrNull()?.let { setAgent(it) }
        isNewChat = true
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
            chatStateManager.updateChatScreenState { ChatScreenState.Loading }
            currentChatMessagesPage = 1
            isLastChatMessagesPage = false
            val tempChatScreenState =
                chatStateManager.lastSuccessScreenState ?: chatScreenState.value
            if (tempChatScreenState is ChatScreenState.Success && tempChatScreenState.isReceivingMessage) {
                webSocketManager.stopMessageStream()
            }
            chatUseCase.getChatMessagesById(
                id, pageSize = chatMessagesPageSize, page = currentChatMessagesPage
            ).combine(
                chatUseCase.getChatById(
                    id = id, withAvatar = true
                )
            ) { messagesResponse, chatResponse ->
                Pair(messagesResponse, chatResponse)
            }.collect { (chatMessagesResponse, chatResponse) ->
                when {
                    chatMessagesResponse is Response.Success && chatResponse is Response.Success -> {
                        isNewChat = false
                        isLastChatMessagesPage =
                            chatMessagesResponse.data.size < chatMessagesPageSize
                        chatHistoryManager.updateHistory(currentChatId = id)
                        chatStateManager.updateChatScreenState {
                            when (tempChatScreenState) {
                                is ChatScreenState.Success -> tempChatScreenState.copy(
                                    messages = chatMessagesResponse.data.toImmutableList(),
                                    chatTitle = title,
                                    isEditingTitle = false,
                                    isReceivingMessage = false,
                                    inputText = "",
                                    currentAgent = chatResponse.data.agent,
                                )

                                else -> ChatScreenState.Success(
                                    agents = persistentListOf(),
                                    messages = chatMessagesResponse.data.toImmutableList(),
                                    chatTitle = title,
                                    isEditingTitle = false,
                                    isReceivingMessage = false,
                                    inputText = "",
                                    currentAgent = chatResponse.data.agent,
                                )
                            }
                        }
                        webSocketManager.setChatId(
                            id,
                            chatResponse.data.agent,
                            chatMessagesResponse.data.isEmpty()
                        )
                        setAgent(chatResponse.data.agent)
                    }

                    chatMessagesResponse is Response.Failure || chatResponse is Response.Failure -> {
                        chatStateManager.updateChatScreenState {
                            ChatScreenState.Error(Res.string.failed_to_load_chat_messages)
                        }
                    }

                    chatMessagesResponse is Response.Loading || chatResponse is Response.Loading -> {
                        chatStateManager.updateChatScreenState { ChatScreenState.Loading }
                    }

                    chatMessagesResponse is Response.Unauthorized || chatResponse is Response.Unauthorized -> {
                        chatStateManager.updateChatScreenState { ChatScreenState.Unauthorized }
                    }

                }
            }
        }
    }

    /**
     * Loads chat messages for a specific chat.
     *
     * @param chatId The ID of the chat to load messages for.
     * @param isInitialLoad Whether this is the initial load of messages. If true, it resets pagination.
     */
    private fun loadChatMessages(chatId: String, isInitialLoad: Boolean = true) {
        viewModelScope.launch {
            if (isInitialLoad) {
                currentChatMessagesPage = 1
                isLastChatMessagesPage = false
            }

            chatUseCase.getChatMessagesById(chatId, currentChatMessagesPage, chatMessagesPageSize)
                .collectLatest { messagesResponse ->
                    when (messagesResponse) {
                        is Response.Success -> {
                            val newMessages = messagesResponse.data
                            isLastChatMessagesPage = newMessages.size < chatMessagesPageSize

                            chatStateManager.updateChatScreenState { currentState ->
                                when (currentState) {
                                    is ChatScreenState.Success -> {
                                        val updatedMessages = if (isInitialLoad) {
                                            newMessages.toImmutableList()
                                        } else {
                                            currentState.messages + newMessages
                                        }
                                        currentState.copy(
                                            messages = updatedMessages.toImmutableList(),
                                            isLoadingMessages = false
                                        )
                                    }

                                    else -> currentState
                                }
                            }

                        }

                        is Response.Loading -> {
                            chatStateManager.updateChatScreenState { currentState ->
                                when (currentState) {
                                    is ChatScreenState.Success -> currentState.copy(
                                        isLoadingMessages = true
                                    )

                                    else -> currentState
                                }
                            }
                        }

                        is Response.Failure -> {
                            // Handle error
                            chatStateManager.updateChatScreenState {
                                ChatScreenState.Error(Res.string.failed_to_load_chat_messages)
                            }
                        }

                        is Response.Unauthorized -> {
                            chatStateManager.updateChatScreenState {
                                ChatScreenState.Unauthorized
                            }
                        }
                    }
                }
        }
    }

    /**
     * Loads more chat messages for the current chat.
     * This function should be called when the user scrolls to the top of the message list.
     */
    fun loadMoreChatMessages() {
        if (!isLastChatMessagesPage && !isNewChat && chatScreenState.value is ChatScreenState.Success && !(chatScreenState.value as ChatScreenState.Success).isLoadingMessages) {
            webSocketManager.getChatId()?.let { chatId ->
                currentChatMessagesPage++
                loadChatMessages(chatId, isInitialLoad = false)
            }
        }
    }

    /**
     * Logs out the current user.
     *
     * @param onLogoutSuccess Callback to be invoked on successful logout
     */
    fun logout(onLogoutSuccess: () -> Unit) {
        val tempChatScreenState = chatStateManager.lastSuccessScreenState ?: chatScreenState.value
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

                    is Response.Unauthorized -> {
                        chatStateManager.updateChatScreenState { ChatScreenState.Unauthorized }
                    }
                }
            }
        }
    }

    fun clearViewModel() {
        chatStateManager.clearChat()
        viewModelScope.launch {
            webSocketManager.disconnect()
        }
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
    fun updateCurrentUser() {
        viewModelScope.launch {
            currentUserUseCase(withAvatar = true).collect { response ->
                _currentUserViewState.value = when (response) {
                    is Response.Success -> {
                        HistoryScreenStates.Success(response.data.toViewState())
                    }

                    is Response.Failure -> HistoryScreenStates.Error
                    is Response.Loading -> HistoryScreenStates.Loading
                    is Response.Unauthorized -> HistoryScreenStates.Unauthorized
                }
            }
        }
    }

    /**
     * Evaluates a message.
     *
     * @param message The message to evaluate
     * @param evaluation The evaluation result (true for positive, false for negative)
     * @param feedback Optional feedback string for the evaluation
     */
    fun evaluateMessage(message: ChatMessageItem, evaluation: Boolean?, feedback: String? = null) {
        viewModelScope.launch {
            if (!webSocketManager.webSocketChatClient?.currentChatId?.value.isNullOrEmpty() && !message.id.isNullOrEmpty()) {
                val result =
                    chatUseCase.evaluateMessage(
                        webSocketManager.webSocketChatClient?.currentChatId?.value!!,
                        message.id,
                        evaluation,
                        feedback
                    )
                debugLog("Evaluation result: $result")
                if (result is Response.Success) {
                    chatStateManager.updateMessageEvaluation(message, evaluation)
                    try {
                        SnackbarHelper.getInstance()
                            .showSnackbar(messageRes = Res.string.message_evaluated)
                    } catch (e: IllegalStateException) {
                        debugLogError("Failed to show snackbar", e)
                    }
                }
            }
        }
    }

    /**
     * Updates the user's avatar with compression if needed.
     *
     * @param imageByteArray The byte array representing the avatar image
     */
    fun updateAvatar(imageByteArray: ByteArray) {
        val tempCurrentUserViewState = _currentUserViewState.value
        val maxSizeBytes = 500 * 1024

        viewModelScope.launch {
            try {
                _currentUserViewState.value = HistoryScreenStates.Loading
                val compressedImage = compressImage(imageByteArray, maxSizeBytes)
                updateProfileAvatarUseCase(compressedImage).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            updateCurrentUser()
                        }

                        is Response.Failure -> {
                            _currentUserViewState.value = tempCurrentUserViewState
                        }

                        is Response.Loading -> {
                            _currentUserViewState.value = HistoryScreenStates.Loading
                        }

                        is Response.Unauthorized -> {
                            debugLogError("Unauthorized to update avatar")
                        }
                    }
                }
            } catch (e: Exception) {
                debugLogError("Failed to compress image", e)
                _currentUserViewState.value = tempCurrentUserViewState
            }
        }
    }

    /**
     * Compresses the image until it's below the maximum size.
     *
     * @param imageBytes Original image bytes
     * @param maxSizeBytes Maximum size in bytes
     * @return Compressed image bytes
     */
    private suspend fun compressImage(imageBytes: ByteArray, maxSizeBytes: Int): ByteArray {
        if (imageBytes.size <= maxSizeBytes) return imageBytes

        var quality = 100
        var compressedBytes = imageBytes
        val bitmap = imageBytes.toImageBitmap()

        while (compressedBytes.size > maxSizeBytes && quality > 5) {
            quality -= 5
            compressedBytes = bitmap.encodeToByteArray(quality) ?: imageBytes
        }

        return compressedBytes
    }

    /**
     * Deletes the user's avatar.
     */
    fun deleteAvatar() {
        val tempCurrentUserViewState = _currentUserViewState.value

        viewModelScope.launch {
            deleteProfileAvatarUseCase().collect { response ->
                when (response) {
                    is Response.Success -> {
                        updateCurrentUser()
                    }

                    is Response.Failure -> {
                        _currentUserViewState.value = tempCurrentUserViewState
                    }

                    is Response.Loading -> {
                        _currentUserViewState.value = HistoryScreenStates.Loading
                    }

                    is Response.Unauthorized -> {
                        debugLogError("Unauthorized to delete avatar")
                    }
                }
            }
        }
    }

    fun setDeleteAvatarVisible(deleteAvatarVisible: Boolean) {
        viewModelScope.launch {
            _deleteAvatarVisible.emit(deleteAvatarVisible)
        }
    }

    /**
     * Called when WebSocket informs that agent is deactivated.
     */
    fun agentDeactivated(agentId: String?) {
        viewModelScope.launch {
            val tempChatScreenState =
                chatStateManager.lastSuccessScreenState ?: chatScreenState.value
            if (webSocketManager.webSocketChatClient?.tempChatId?.value != null && tempChatScreenState is ChatScreenState.Success && tempChatScreenState.messages.isNotEmpty()) {
                if (tempChatScreenState.isReceivingMessage) {
                    while (chatStateManager.lastSuccessScreenState?.isReceivingMessage == true) {
                        delay(200)
                    }
                }
                updateAgents()
                updateAgentForChatId(
                    webSocketManager.webSocketChatClient?.tempChatId?.value!!,
                )
            } else {
                updateAgents()
                newChat()
            }
        }
    }

    private suspend fun updateAgentForChatId(id: String) {
        val tempChatScreenState = chatStateManager.lastSuccessScreenState ?: chatScreenState.value
        chatUseCase.getChatById(id, withAvatar = true).collectLatest { chatResponse ->
            when (chatResponse) {
                is Response.Success -> {
                    chatStateManager.updateChatScreenState {
                        when (tempChatScreenState) {
                            is ChatScreenState.Success -> {
                                tempChatScreenState.copy(
                                    currentAgent = chatResponse.data.agent
                                )
                            }

                            else -> tempChatScreenState
                        }
                    }
                }

                else -> {}
            }
        }
    }

    suspend fun updateAgents() {
        val tempChatScreenState = chatStateManager.lastSuccessScreenState ?: chatScreenState.value
        chatUseCase.getAgents(withAvatar = true).collectLatest { agentsResponse ->
            when (agentsResponse) {
                is Response.Success -> {
                    chatStateManager.updateChatScreenState {
                        when (tempChatScreenState) {
                            is ChatScreenState.Success -> {
                                tempChatScreenState.copy(
                                    agents = agentsResponse.data.toImmutableList(),
                                )
                            }

                            else -> {
                                tempChatScreenState
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    fun updateLastMessageId(messageId: String?) {
        val tempChatScreenState = chatStateManager.lastSuccessScreenState ?: chatScreenState.value
        viewModelScope.launch {
            if (!messageId.isNullOrEmpty()) {
                chatStateManager.updateChatScreenState {
                    when (tempChatScreenState) {
                        is ChatScreenState.Success -> {
                            val tempMessages = tempChatScreenState.messages.toMutableList()
                            if (tempMessages.isNotEmpty()) {
                                tempMessages[0] = tempMessages[0].copy(id = messageId)
                            }
                            tempChatScreenState.copy(
                                messages = tempMessages.toImmutableList()
                            )
                        }

                        else -> tempChatScreenState
                    }
                }
            }
        }
    }
}