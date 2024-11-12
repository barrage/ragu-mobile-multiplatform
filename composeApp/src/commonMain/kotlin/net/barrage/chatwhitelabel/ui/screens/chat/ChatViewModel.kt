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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.model.HistoryElement
import net.barrage.chatwhitelabel.domain.usecase.agents.GetAgentsUseCase
import net.barrage.chatwhitelabel.domain.usecase.auth.LogoutUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.DeleteChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.HistoryByIdUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.HistoryUseCase
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

class ChatViewModel(
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    private val historyUseCase: HistoryUseCase,
    private val historyByIdUseCase: HistoryByIdUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val getAgentsUseCase: GetAgentsUseCase,
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

    fun addMessage(message: String) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success ->
                    currentState.copy(
                        messages = (currentState.messages + message).toImmutableList()
                    )

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
            addMessage(currentState.inputText)
            webSocketChatClient?.sendMessage(currentState.inputText)
            updateInputText("")
        }
    }

    fun updateLastMessage(message: String) {
        updateChatScreenState { currentState ->
            when (currentState) {
                is ChatScreenState.Success -> {
                    if (currentState.messages.isNotEmpty()) {
                        val updatedMessages = currentState.messages.toMutableList()
                        updatedMessages[updatedMessages.lastIndex] += message
                        currentState.copy(messages = updatedMessages.toImmutableList())
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

    fun getHistoryChatById(id: String, title: String) {
        viewModelScope.launch {
            val response = historyByIdUseCase.invoke(id)
            if (response is Response.Success) {
                webSocketChatClient?.setChatId(id)
                updateChatScreenState { currentState ->
                    when (currentState) {
                        is ChatScreenState.Success ->
                            currentState.copy(
                                messages = response.data.map { it.content }.toImmutableList(),
                                chatTitle = title,
                            )

                        else ->
                            ChatScreenState.Success(
                                agents = persistentListOf(),
                                messages = response.data.map { it.content }.toImmutableList(),
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
                coreComponent.appPreferences.clear()
                onLogoutSuccess()
            }
        }
    }

    suspend fun updateHistory() {
        historyViewState =
            when (val response = historyUseCase.invoke(1, 50)) {
                is Response.Success -> {
                    historyViewState.copy(
                        history =
                            HistoryScreenStates.Success(
                                HistoryViewState(
                                    elements =
                                        mapElementsByTimePeriod(
                                                response.data.elements,
                                                webSocketChatClient?.currentChatId?.value,
                                            )
                                            .toImmutableMap(),
                                    itemsNum = response.data.itemsNum,
                                )
                            )
                    )
                }

                is Response.Failure -> historyViewState.copy(history = HistoryScreenStates.Error)
                Response.Loading -> historyViewState.copy(history = HistoryScreenStates.Loading)
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
        elements: List<HistoryElement>,
        currentChatId: String?,
    ): ImmutableMap<String?, ImmutableList<HistoryElement>> {
        val now: Instant = Clock.System.now()
        val today: LocalDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val yesterday: LocalDate = today.minus(1, DateTimeUnit.DAY)
        val last7days: LocalDate = today.minus(7, DateTimeUnit.DAY)
        val last30days: LocalDate = today.minus(1, DateTimeUnit.MONTH)
        val lastYear: LocalDate = today.minus(1, DateTimeUnit.YEAR)

        val groupedElements =
            elements
                .map { it.copy(isSelected = it.id == currentChatId) }
                .groupBy { element ->
                    val currentElement =
                        element.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    when {
                        currentElement >= today -> HistoryTimePeriod.TODAY.label
                        currentElement >= yesterday -> HistoryTimePeriod.YESTERDAY.label
                        currentElement >= last7days -> HistoryTimePeriod.LAST_7_DAYS.label
                        currentElement >= last30days -> HistoryTimePeriod.LAST_30_DAYS.label
                        currentElement >= lastYear -> HistoryTimePeriod.LAST_YEAR.label
                        else -> null
                    }
                }
                .filterKeys { it != null }
                .mapKeys { it.key }

        val finalGroupedElements =
            groupedElements.mapValues { entry -> entry.value.toImmutableList() }

        return finalGroupedElements.toImmutableMap().toImmutableMap()
    }
}
