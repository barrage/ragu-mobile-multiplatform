@file:Suppress("TooManyFunctions", "LongParameterList")

package net.barrage.chatwhitelabel.ui.screens.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.usecase.agents.GetAgentsUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.DeleteChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.HistoryByIdUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.HistoryUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.UpdateChatTitleUseCase
import net.barrage.chatwhitelabel.domain.usecase.user.CurrentUserUseCase
import net.barrage.chatwhitelabel.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.chatwhitelabel.ui.screens.history.HistoryModalDrawerContentViewState
import net.barrage.chatwhitelabel.ui.screens.history.HistoryScreenStates
import net.barrage.chatwhitelabel.utils.BluePrimary
import net.barrage.chatwhitelabel.utils.BrownPrimary
import net.barrage.chatwhitelabel.utils.GreenPrimary
import net.barrage.chatwhitelabel.utils.LimePrimary
import net.barrage.chatwhitelabel.utils.MagentaPrimary
import net.barrage.chatwhitelabel.utils.OrangePrimary
import net.barrage.chatwhitelabel.utils.RedPrimary
import net.barrage.chatwhitelabel.utils.SagePrimary
import net.barrage.chatwhitelabel.utils.TealPrimary
import net.barrage.chatwhitelabel.utils.VioletPrimary
import net.barrage.chatwhitelabel.utils.YellowPrimary
import net.barrage.chatwhitelabel.utils.chat.WebSocketChatClient

class ChatViewModel(
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    private val historyUseCase: HistoryUseCase,
    private val historyByIdUseCase: HistoryByIdUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val getAgentsUseCase: GetAgentsUseCase,
) : ViewModel() {
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> = _messages

    private val _agents = mutableStateListOf<Agent>()
    val agents: List<Agent> = _agents

    private val _inputText = mutableStateOf("")
    val inputText: String
        get() = _inputText.value

    private val _isSendEnabled = mutableStateOf(false)
    val isSendEnabled: Boolean
        get() = _isSendEnabled.value

    private val _isReceivingMessage = mutableStateOf(false)
    val isReceivingMessage: Boolean
        get() = _isReceivingMessage.value

    private val _isEditingTitle = mutableStateOf(false)
    val isEditingTitle: Boolean
        get() = _isEditingTitle.value

    private val _chatTitle = mutableStateOf("New chat")
    val chatTitle: String
        get() = _chatTitle.value

    private val _selectedAgent = mutableStateOf<Agent?>(null)
    val selectedAgent: Agent?
        get() = _selectedAgent.value

    var webSocketChatClient: WebSocketChatClient? = null
        private set

    var historyViewState by
        mutableStateOf(
            HistoryModalDrawerContentViewState(
                persistentListOf(
                    White,
                    SagePrimary,
                    TealPrimary,
                    BluePrimary,
                    VioletPrimary,
                    LimePrimary,
                    GreenPrimary,
                    YellowPrimary,
                    OrangePrimary,
                    RedPrimary,
                    MagentaPrimary,
                    BrownPrimary,
                ),
                HistoryScreenStates.Idle,
                HistoryScreenStates.Idle,
            )
        )
        private set

    fun loadAllData() {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch { updateHistory() }
            CoroutineScope(Dispatchers.IO).launch { updateCurrentUser() }
            val agents = getAgentsUseCase()
            if (agents is Response.Success) {
                _agents.clear()
                _agents.addAll(agents.data)
                _selectedAgent.value = agents.data.firstOrNull()
            } else {
                // Handle error
            }
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun addMessage(message: String) {
        _messages.add(message)
    }

    fun setSendEnabled(enabled: Boolean) {
        _isSendEnabled.value = enabled
    }

    fun setReceivingMessage(receiving: Boolean) {
        _isReceivingMessage.value = receiving
    }

    fun sendMessage() {
        if (inputText.isEmpty()) return
        _messages.add(inputText)
        webSocketChatClient?.sendMessage(inputText)
        _inputText.value = ""
    }

    fun updateLastMessage(message: String) {
        if (messages.isNotEmpty()) {
            val tempMessages = messages.toMutableList()
            var tempMessage = tempMessages[tempMessages.size - 1]
            tempMessage += message
            tempMessages[tempMessages.size - 1] = tempMessage
            _messages.clear()
            _messages.addAll(tempMessages)
        }
    }

    fun initializeWebSocketClient(callback: ReceiveMessageCallback, scope: CoroutineScope) {
        scope.launch {
            val token = webSocketTokenUseCase()
            if (token is Response.Success) {
                webSocketChatClient =
                    WebSocketChatClient(callback, scope, token.data, _selectedAgent)
            }
        }
    }

    fun setChatTitle(title: String) {
        _chatTitle.value = title
    }

    fun setEditingTitle(editing: Boolean) {
        _isEditingTitle.value = editing
    }

    fun updateTitle() {
        viewModelScope.launch {
            if (webSocketChatClient?.currentChatId?.value.isNullOrEmpty()) return@launch
            val response =
                updateChatTitleUseCase(webSocketChatClient?.currentChatId?.value!!, chatTitle)
            if (response is Response.Success) {
                setEditingTitle(false)
            } else {
                // Handle error
            }
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            if (webSocketChatClient?.currentChatId?.value.isNullOrEmpty()) return@launch
            val response = deleteChatUseCase(webSocketChatClient?.currentChatId?.value!!)
            if (response is Response.Success) {
                clearChat()
            } else {
                // Handle error
            }
        }
    }

    fun setAgent(agent: Agent) {
        _selectedAgent.value = agent
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
                _messages.clear()
                _messages.addAll(response.data.map { it.content })
                _chatTitle.value = title
            } else {
                // Handle error
            }
        }
    }

    private fun clearChat() {
        _messages.clear()
        _chatTitle.value = "New Chat"
        _isEditingTitle.value = false
        _isReceivingMessage.value = false
        _inputText.value = ""
    }

    suspend fun updateHistory() {
        historyViewState =
            when (val response = historyUseCase.invoke(1, 10)) {
                is Response.Success -> {
                    historyViewState.copy(history = HistoryScreenStates.Success(response.data))
                }

                is Response.Failure -> {
                    historyViewState.copy(history = HistoryScreenStates.Error)
                }

                Response.Loading -> {
                    historyViewState.copy(history = HistoryScreenStates.Loading)
                }
            }
    }

    private suspend fun updateCurrentUser() {
        historyViewState =
            when (val response = currentUserUseCase.invoke()) {
                is Response.Success -> {
                    historyViewState.copy(currentUser = HistoryScreenStates.Success(response.data))
                }

                is Response.Failure -> {
                    historyViewState.copy(currentUser = HistoryScreenStates.Error)
                }

                Response.Loading -> {
                    historyViewState.copy(currentUser = HistoryScreenStates.Loading)
                }
            }
    }
}
