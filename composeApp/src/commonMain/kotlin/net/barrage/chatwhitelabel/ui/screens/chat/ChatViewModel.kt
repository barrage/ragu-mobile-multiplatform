@file:Suppress("TooManyFunctions")

package net.barrage.chatwhitelabel.ui.screens.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.failed_to_load_agents
import chatwhitelabel.composeapp.generated.resources.new_chat
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import org.jetbrains.compose.resources.StringResource

class ChatViewModel(
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    private val chatUseCase: ChatUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _chatScreenState = MutableStateFlow<ChatScreenState>(ChatScreenState.Idle)
    val chatScreenState: StateFlow<ChatScreenState> = _chatScreenState.asStateFlow()

    var webSocketChatClient: WebSocketChatClient? = null
        private set

    private val _historyViewState =
        MutableStateFlow(
            HistoryModalDrawerContentViewState(
                ThemeColors,
                PaletteVariants,
                HistoryScreenStates.Idle,
            )
        )
    val historyViewState: StateFlow<HistoryModalDrawerContentViewState> =
        _historyViewState.asStateFlow()

    var selectedAgent: MutableState<Agent?> = mutableStateOf(null)
        private set

    private val _currentUserViewState =
        MutableStateFlow<HistoryScreenStates<ProfileViewState>>(HistoryScreenStates.Idle)
    val currentUserViewState: StateFlow<HistoryScreenStates<ProfileViewState>> =
        _currentUserViewState.asStateFlow()

    private var shouldUpdateHistory = true
    private var tempChatTitle: String = ""

    fun loadAllData() {
        viewModelScope.launch {
            _chatScreenState.value = ChatScreenState.Loading
            _historyViewState.value =
                historyViewState.value.copy(history = HistoryScreenStates.Loading)
            _currentUserViewState.value = HistoryScreenStates.Loading
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
                _chatScreenState.value = ChatScreenState.Error(Res.string.failed_to_load_agents)
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
            SenderType.ASSISTANT,
            SenderType.ERROR -> setSendEnabled(true)

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
        val currentState = chatScreenState.value
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

    fun setChatTitle(title: String, chatId: String? = null) {
        if (webSocketChatClient?.currentChatId?.value == chatId || chatId.isNullOrEmpty()) {
            updateChatScreenState { currentState ->
                when (currentState) {
                    is ChatScreenState.Success -> currentState.copy(chatTitle = title)
                    else -> currentState
                }
            }
        }
    }

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

    fun updateTitle() {
        viewModelScope.launch {
            val currentState = chatScreenState.value
            if (
                currentState is ChatScreenState.Success &&
                    !webSocketChatClient?.currentChatId?.value.isNullOrEmpty()
            ) {
                currentState.chatTitle?.let { chatTitle ->
                    val response =
                        chatUseCase.updateChatTitle(
                            webSocketChatClient?.currentChatId?.value!!,
                            chatTitle,
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
                val tempChatScreenState = chatScreenState.value
                _chatScreenState.value = ChatScreenState.Loading
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
        val currentState = chatScreenState.value
        if (currentState is ChatScreenState.Success && currentState.isReceivingMessage) {
            webSocketChatClient?.stopMessageStream()
        }
        clearChat()
    }

    fun onChatClosed() {
        clearChat()
    }

    fun getChatById(id: String, title: String) {
        viewModelScope.launch {
            val tempChatScreenState = chatScreenState.value
            if (
                tempChatScreenState is ChatScreenState.Success &&
                    tempChatScreenState.isReceivingMessage
            ) {
                webSocketChatClient?.stopMessageStream()
            }
            webSocketChatClient?.isChatOpen?.value = false
            _chatScreenState.value = ChatScreenState.Loading
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
        _chatScreenState.value = ChatScreenState.Loading
        updateChatScreenState { currentState ->
            when (stateToUse) {
                is ChatScreenState.Success ->
                    stateToUse.copy(
                        messages = persistentListOf(),
                        chatTitleRes = Res.string.new_chat,
                        chatTitle = null,
                        isEditingTitle = false,
                        isReceivingMessage = false,
                        inputText = "",
                        isAgentActive = true,
                    )

                else -> currentState
            }
        }
        webSocketChatClient?.setChatId(null)
        webSocketChatClient?.isChatOpen?.value = false
        updateHistory()
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            val response = logoutUseCase()
            if (response is Response.Success) {
                _chatScreenState.value = ChatScreenState.Idle
                webSocketChatClient?.disconnect()
                webSocketChatClient = null
                _historyViewState.value =
                    HistoryModalDrawerContentViewState(
                        ThemeColors,
                        PaletteVariants,
                        HistoryScreenStates.Idle,
                    )
                selectedAgent.value = null
                _currentUserViewState.value = HistoryScreenStates.Idle

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
                        _historyViewState.value =
                            historyViewState.value.copy(
                                history =
                                    HistoryScreenStates.Success(
                                        HistoryViewState(elements = mappedElements)
                                    )
                            )
                    }

                    is Response.Failure ->
                        _historyViewState.value =
                            historyViewState.value.copy(history = HistoryScreenStates.Error)

                    Response.Loading -> {}
                }
            }
        } else {
            shouldUpdateHistory = true
        }
    }

    private suspend fun updateCurrentUser() {
        _currentUserViewState.value =
            when (val response = currentUserUseCase.invoke()) {
                is Response.Success -> HistoryScreenStates.Success(response.data.toViewState())

                is Response.Failure -> HistoryScreenStates.Error

                Response.Loading -> HistoryScreenStates.Loading
            }
    }

    private fun updateChatScreenState(update: (ChatScreenState) -> ChatScreenState) {
        _chatScreenState.value = update(chatScreenState.value)
    }

    private fun mapElementsByTimePeriod(
        elements: List<ChatHistoryItem>,
        currentChatId: String?,
    ): ImmutableMap<StringResource?, ImmutableList<ChatHistoryItem>> {
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
