@file:Suppress("TooManyFunctions")

package net.barrage.chatwhitelabel.ui.screens.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import net.barrage.chatwhitelabel.data.remote.dto.history.SenderType
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.model.ChatHistoryItem
import net.barrage.chatwhitelabel.domain.model.ChatMessageItem
import net.barrage.chatwhitelabel.domain.usecase.auth.LogoutUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.ChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.user.CurrentUserUseCase
import net.barrage.chatwhitelabel.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.chatwhitelabel.ui.screens.history.HistoryModalDrawerContentViewState
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.ui.screens.history.HistoryTimePeriod
import net.barrage.chatwhitelabel.ui.screens.history.HistoryViewState
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.chatwhitelabel.utils.PaletteVariants
import net.barrage.chatwhitelabel.utils.ThemeColors
import net.barrage.chatwhitelabel.utils.chat.WebSocketChatClient
import net.barrage.chatwhitelabel.utils.coreComponent
import net.barrage.chatwhitelabel.utils.debugLog

class ChatViewModel(
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    private val chatUseCase: ChatUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    var chatScreenState by mutableStateOf<ChatScreenState>(ChatScreenState.Idle)
        private set

    var webSocketChatClient: WebSocketChatClient? = null
        private set

    var historyViewState by
        mutableStateOf(
            HistoryModalDrawerContentViewState(
                ThemeColors,
                PaletteVariants,
                HistoryScreenStates.Idle,
            )
        )

    var selectedAgent: MutableState<Agent?> = mutableStateOf(null)
        private set

    var currentUserViewState by
        mutableStateOf<HistoryScreenStates<ProfileViewState>>(HistoryScreenStates.Idle)
        private set

    private var shouldUpdateHistory = true
    private var tempChatTitle: String = ""

    fun loadAllData() {
        viewModelScope.launch {
            chatScreenState = ChatScreenState.Loading
            historyViewState = historyViewState.copy(history = HistoryScreenStates.Loading)
            currentUserViewState = HistoryScreenStates.Loading
            launch { updateHistory() }
            launch { updateCurrentUser() }

            val agentsResponse = chatUseCase.getAgents()
            if (agentsResponse is Response.Success) {
                updateChatScreenState { currentState ->
                    when (currentState) {
                        is ChatScreenState.Success -> {
                            selectedAgent.value = agentsResponse.data.firstOrNull()
                            currentState.copy(
                                agents = agentsResponse.data.toImmutableList(),
                                isAgentActive = true,
                            )
                        }

                        else -> {
                            selectedAgent.value = agentsResponse.data.firstOrNull()
                            ChatScreenState.Success(
                                agents = agentsResponse.data.toImmutableList(),
                                messages = persistentListOf(),
                                isAgentActive = true,
                            )
                        }
                    }
                }
            } else {
                chatScreenState = ChatScreenState.Error("Failed to load agents")
            }
        }
    }

    fun updateInputText(text: String) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(inputText = text)
                else -> currentState
            }
        }
    }

    fun addMessage(messageContent: String, senderType: SenderType) {
        when (senderType) {
            SenderType.ASSISTANT -> setSendEnabled(true)
            SenderType.USER -> setSendEnabled(false)
        }
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

    fun setSendEnabled(enabled: Boolean) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(isSendEnabled = enabled)
                else -> currentState
            }
        }
    }

    fun setReceivingMessage(receiving: Boolean) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(isReceivingMessage = receiving)
                else -> currentState
            }
        }
    }

    fun sendMessage() {
        val currentState = chatScreenState
        if (currentState is ChatScreenState.Success && currentState.inputText.isNotEmpty()) {
            addMessage(currentState.inputText, SenderType.USER)
            webSocketChatClient?.sendMessage(currentState.inputText)
            updateInputText("")
        }
    }

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

    fun initializeWebSocketClient(callback: ReceiveMessageCallback, scope: CoroutineScope) {
        scope.launch {
            val token = webSocketTokenUseCase()
            if (token is Response.Success) {
                if (webSocketChatClient == null) {
                    webSocketChatClient =
                        WebSocketChatClient(callback, scope, selectedAgent, webSocketTokenUseCase)
                }
            }
        }
    }

    fun setChatTitle(title: String) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> currentState.copy(chatTitle = title)
                else -> currentState
            }
        }
    }

    fun setEditingTitle(editing: Boolean) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    if (editing) {
                        tempChatTitle = currentState.chatTitle
                    }
                    currentState.copy(isEditingTitle = editing)
                }

                else -> currentState
            }
        }
    }

    fun updateTitle() {
        viewModelScope.launch {
            val currentState = chatScreenState
            if (
                currentState is ChatScreenState.Success &&
                    !webSocketChatClient?.currentChatId?.value.isNullOrEmpty()
            ) {
                val response =
                    chatUseCase.updateChatTitle(
                        webSocketChatClient?.currentChatId?.value!!,
                        currentState.chatTitle,
                    )
                if (response is Response.Success) {
                    setEditingTitle(false)
                    tempChatTitle = "" // Clear temporary title after successful update
                } else {
                    // Handle error
                }
            }
        }
    }

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

    fun deleteChat() {
        viewModelScope.launch {
            if (!webSocketChatClient?.currentChatId?.value.isNullOrEmpty()) {
                val tempChatScreenState = chatScreenState
                chatScreenState = ChatScreenState.Loading
                val response = chatUseCase.deleteChat(webSocketChatClient?.currentChatId?.value!!)
                if (response is Response.Success) {
                    clearChat(tempChatScreenState)
                } else {
                    // Handle error
                }
            }
        }
    }

    fun setAgent(agent: Agent) {
        selectedAgent.value = agent
    }

    fun newChat() {
        if (webSocketChatClient?.isChatOpen?.value == true) {
            webSocketChatClient?.closeChat()
        } else {
            clearChat()
        }
    }

    fun onChatClosed() {
        clearChat()
    }

    fun getChatById(id: String, title: String) {
        viewModelScope.launch {
            val tempChatScreenState = chatScreenState
            chatScreenState = ChatScreenState.Loading
            val chatMessagesResponse = chatUseCase.getChatMessagesById(id)
            val chatResponse = chatUseCase.getChatById(id)
            if (chatMessagesResponse is Response.Success && chatResponse is Response.Success) {
                webSocketChatClient?.setChatId(id)
                updateHistory()
                shouldUpdateHistory = false
                updateChatScreenState {
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
            } else {
                // Handle error
            }
        }
    }

    private fun clearChat(tempChatScreenState: ChatScreenState? = null) {
        val stateToUse = tempChatScreenState ?: chatScreenState
        chatScreenState = ChatScreenState.Loading
        updateChatScreenState { currentState ->
            when (stateToUse) {
                is ChatScreenState.Success ->
                    stateToUse.copy(
                        messages = persistentListOf(),
                        chatTitle = "New Chat",
                        isEditingTitle = false,
                        isReceivingMessage = false,
                        inputText = "",
                        isAgentActive = true,
                    )

                else -> currentState
            }
        }
        webSocketChatClient?.setChatId(null)
        updateHistory()
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            val response = logoutUseCase()
            if (response is Response.Success) {
                chatScreenState = ChatScreenState.Idle
                webSocketChatClient?.disconnect()
                webSocketChatClient = null
                historyViewState =
                    HistoryModalDrawerContentViewState(
                        ThemeColors,
                        PaletteVariants,
                        HistoryScreenStates.Idle,
                    )
                selectedAgent.value = null
                currentUserViewState = HistoryScreenStates.Idle

                coreComponent.appPreferences.clear()

                onLogoutSuccess()
            } else {
                // Handle logout failure
            }
        }
    }

    fun updateHistory() {
        if (shouldUpdateHistory) {
            viewModelScope.launch {
                // historyViewState = historyViewState.copy(history = HistoryScreenStates.Loading)
                when (val response = chatUseCase.getChatHistory(1, 50)) {
                    is Response.Success -> {
                        val mappedElements =
                            mapElementsByTimePeriod(
                                response.data,
                                webSocketChatClient?.currentChatId?.value,
                            )
                        historyViewState =
                            historyViewState.copy(
                                history =
                                    HistoryScreenStates.Success(
                                        HistoryViewState(elements = mappedElements)
                                    )
                            )
                    }

                    is Response.Failure ->
                        historyViewState =
                            historyViewState.copy(history = HistoryScreenStates.Error)

                    Response.Loading -> {}
                }
            }
        } else {
            shouldUpdateHistory = true
        }
    }

    private suspend fun updateCurrentUser() {
        currentUserViewState =
            when (val response = currentUserUseCase.invoke()) {
                is Response.Success -> HistoryScreenStates.Success(response.data.toViewState())

                is Response.Failure -> HistoryScreenStates.Error

                Response.Loading -> HistoryScreenStates.Loading
            }
    }

    private fun updateChatScreenState(update: (ChatScreenState) -> ChatScreenState) {
        chatScreenState = update(chatScreenState)
    }

    private fun mapElementsByTimePeriod(
        elements: List<ChatHistoryItem>,
        currentChatId: String?,
    ): ImmutableMap<String?, ImmutableList<ChatHistoryItem>> {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val timePeriods =
            listOf(
                HistoryTimePeriod.TODAY to today,
                HistoryTimePeriod.YESTERDAY to today.minus(1, DateTimeUnit.DAY),
                HistoryTimePeriod.LAST_7_DAYS to today.minus(7, DateTimeUnit.DAY),
                HistoryTimePeriod.LAST_30_DAYS to today.minus(1, DateTimeUnit.MONTH),
                HistoryTimePeriod.LAST_YEAR to today.minus(1, DateTimeUnit.YEAR),
            )

        return elements
            .asSequence()
            .map { it.copy(isSelected = it.id == currentChatId) }
            .groupBy { element ->
                val elementDate =
                    element.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
                timePeriods.firstOrNull { (_, date) -> elementDate >= date }?.first?.label
            }
            .mapValues { (_, value) -> value.toImmutableList() }
            .toImmutableMap()
    }

    fun evaluateMessage(message: ChatMessageItem, evaluation: Boolean) {
        viewModelScope.launch {
            if (!message.chatId.isNullOrEmpty() && !message.id.isNullOrEmpty()) {
                val result = chatUseCase.evaluateMessage(message.chatId, message.id, evaluation)
                debugLog("Evaluation result: $result")
            }
        }
    }
}
