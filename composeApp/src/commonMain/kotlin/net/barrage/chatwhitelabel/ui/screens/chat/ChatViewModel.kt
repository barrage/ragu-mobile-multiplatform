@file:Suppress("TooManyFunctions")

package net.barrage.chatwhitelabel.ui.screens.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.usecase.chat.DeleteChatUseCase
import net.barrage.chatwhitelabel.domain.usecase.chat.UpdateChatTitleUseCase
import net.barrage.chatwhitelabel.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.chatwhitelabel.utils.chat.WebSocketChatClient

class ChatViewModel(
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
) : ViewModel() {
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> = _messages

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

    private val _isChatOpen = mutableStateOf(false)
    val isChatOpen: Boolean
        get() = _isChatOpen.value

    private val _chatTitle = mutableStateOf("New chat")
    val chatTitle: String
        get() = _chatTitle.value

    var webSocketChatClient: WebSocketChatClient? = null
        private set

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
                webSocketChatClient = WebSocketChatClient(callback, scope, token.data)
            }
        }
    }

    fun setChatTitle(title: String) {
        _chatTitle.value = title
    }

    fun setEditingTitle(editing: Boolean) {
        _isEditingTitle.value = editing
    }

    fun setChatOpen(isChatOpen: Boolean) {
        _isChatOpen.value = isChatOpen
    }

    fun updateTitle() {
        viewModelScope.launch {
            if (isChatOpen.not()) return@launch
            val response =
                updateChatTitleUseCase(webSocketChatClient?.currentChatId.orEmpty(), chatTitle)
            if (response is Response.Success) {
                setEditingTitle(false)
            } else {
                // Handle error
            }
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            val response = deleteChatUseCase(webSocketChatClient?.currentChatId.orEmpty())
            if (response is Response.Success) {
                // Handle success
            } else {
                // Handle error
            }
        }
    }
}
