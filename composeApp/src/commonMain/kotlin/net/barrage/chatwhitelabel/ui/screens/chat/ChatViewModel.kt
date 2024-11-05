package net.barrage.chatwhitelabel.ui.screens.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import net.barrage.chatwhitelabel.utils.WebSocketChatClient

class ChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> = _messages

    private val _inputText = mutableStateOf("")
    val inputText: String
        get() = _inputText.value

    private val _isSendEnabled = mutableStateOf(false)
    val isSendEnabled: Boolean
        get() = _isSendEnabled.value

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
        webSocketChatClient = WebSocketChatClient(callback, scope)
    }
}
