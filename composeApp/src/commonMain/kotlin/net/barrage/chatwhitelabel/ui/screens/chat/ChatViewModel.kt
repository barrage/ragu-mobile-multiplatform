@file:Suppress("TooManyFunctions", "LongParameterList")

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
import net.barrage.chatwhitelabel.domain.usecase.agents.GetAgentsUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LogoutUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.DeleteChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.EvaluateMessageUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.GetChatByIdUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.GetChatHistoryUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.UpdateChatTitleUseCase
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
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val getChatByIdUseCase: GetChatByIdUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val getAgentsUseCase: GetAgentsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val evaluateMessageUseCase: EvaluateMessageUseCase,
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

    fun loadAllData() {
        viewModelScope.launch {
            chatScreenState = ChatScreenState.Loading
            historyViewState = historyViewState.copy(history = HistoryScreenStates.Loading)
            currentUserViewState = HistoryScreenStates.Loading
            launch { updateHistory() }
            launch { updateCurrentUser() }

            val agentsResponse = getAgentsUseCase()
            if (agentsResponse is Response.Success) {
                updateChatScreenState { currentState ->
                    when (currentState) {
                        is ChatScreenState.Success -> {
                            selectedAgent.value = agentsResponse.data.firstOrNull()
                            currentState.copy(agents = agentsResponse.data.toImmutableList())
                        }

                        else -> {
                            selectedAgent.value = agentsResponse.data.firstOrNull()
                            ChatScreenState.Success(
                                agents = agentsResponse.data.toImmutableList(),
                                messages = persistentListOf(),
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
            when (currentState) {
                is ChatScreenState.Success -> {
                    if (currentState.messages.isNotEmpty()) {
                        val tempMessages = currentState.messages.toMutableList()
                        if (currentState.messages.last().senderType == SenderType.ASSISTANT) {
                            val tempMessage = tempMessages[tempMessages.lastIndex].content + message
                            tempMessages[tempMessages.lastIndex] =
                                currentState.messages[tempMessages.lastIndex].copy(
                                    content = tempMessage
                                )
                            currentState.copy(messages = tempMessages.toImmutableList())
                        } else {
                            tempMessages.add(
                                ChatMessageItem(
                                    content = message,
                                    senderType = SenderType.ASSISTANT,
                                )
                            )
                            currentState.copy(messages = tempMessages.toImmutableList())
                        }
                    } else {
                        currentState
                    }
                }

                else -> currentState
            }
        }
    }

    fun initializeWebSocketClient(callback: ReceiveMessageCallback, scope: CoroutineScope) {
        scope.launch {
            val token = webSocketTokenUseCase()
            if (token is Response.Success) {
                webSocketChatClient =
                    WebSocketChatClient(callback, scope, token.data, selectedAgent)
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
                is ChatScreenState.Success -> currentState.copy(isEditingTitle = editing)
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
                    updateChatTitleUseCase(
                        webSocketChatClient?.currentChatId?.value!!,
                        currentState.chatTitle,
                    )
                if (response is Response.Success) {
                    setEditingTitle(false)
                } else {
                    // Handle error
                }
            }
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            if (!webSocketChatClient?.currentChatId?.value.isNullOrEmpty()) {
                val response = deleteChatUseCase(webSocketChatClient?.currentChatId?.value!!)
                if (response is Response.Success) {
                    clearChat()
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
            val response = getChatByIdUseCase.invoke(id)
            if (response is Response.Success) {
                webSocketChatClient?.setChatId(id)
                updateChatScreenState {
                    when (tempChatScreenState) {
                        is ChatScreenState.Success ->
                            tempChatScreenState.copy(
                                messages = response.data.toImmutableList(),
                                chatTitle = title,
                            )

                        else ->
                            ChatScreenState.Success(
                                agents = persistentListOf(),
                                messages = response.data.toImmutableList(),
                                chatTitle = title,
                            )
                    }
                }
            } else {
                // Handle error
            }
        }
    }

    private fun clearChat() {
        webSocketChatClient?.setChatId(null)
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success ->
                    currentState.copy(
                        messages = persistentListOf(),
                        chatTitle = "New Chat",
                        isEditingTitle = false,
                        isReceivingMessage = false,
                        inputText = "",
                    )

                else -> currentState
            }
        }
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
        viewModelScope.launch {
            historyViewState = historyViewState.copy(history = HistoryScreenStates.Loading)
            when (val response = getChatHistoryUseCase.invoke(1, 50)) {
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
                    historyViewState = historyViewState.copy(history = HistoryScreenStates.Error)
                Response.Loading -> {}
            }
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
                val result = evaluateMessageUseCase(message.chatId, message.id, evaluation)
                debugLog("Evaluation result: $result")
            }
        }
    }
}
