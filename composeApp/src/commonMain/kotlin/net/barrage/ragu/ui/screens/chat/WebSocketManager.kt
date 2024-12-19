package net.barrage.ragu.ui.screens.chat

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.ragu.utils.chat.WebSocketChatClient

/**
 * Manages WebSocket connections and operations.
 *
 * @property webSocketTokenUseCase Use case for obtaining WebSocket tokens
 */
class WebSocketManager(private val webSocketTokenUseCase: WebSocketTokenUseCase) {

    var webSocketChatClient: WebSocketChatClient? = null
        private set

    private lateinit var scope: CoroutineScope
    private lateinit var callback: ReceiveMessageCallback
    private lateinit var selectedAgent: MutableState<Agent?>

    /**
     * Initializes the WebSocket client.
     *
     * @param callback The callback to handle received messages
     * @param scope The coroutine scope to use for the WebSocket client
     * @param selectedAgent The currently selected agent
     */
    suspend fun initializeWebSocketClient(
        callback: ReceiveMessageCallback,
        scope: CoroutineScope,
        selectedAgent: MutableState<Agent?>
    ) {
        this.callback = callback
        this.scope = scope
        this.selectedAgent = selectedAgent

        val token = webSocketTokenUseCase()
        if (token is net.barrage.ragu.domain.Response.Success) {
            if (webSocketChatClient == null) {
                webSocketChatClient =
                    WebSocketChatClient(callback, scope, selectedAgent, webSocketTokenUseCase)
            }
        }
    }

    /**
     * Reconnects the WebSocket client.
     */
    fun reconnect() {
        scope.launch {
            disconnect()
            initializeWebSocketClient(callback, scope, selectedAgent)
        }
    }

    /**
     * Sends a message through the WebSocket client.
     *
     * @param message The message to send
     */
    fun sendMessage(message: String) {
        webSocketChatClient?.sendMessage(message)
    }

    /**
     * Stops the message stream.
     */
    fun stopMessageStream() {
        webSocketChatClient?.stopMessageStream()
    }

    /**
     * Sets the chat ID for the WebSocket client.
     *
     * @param chatId The chat ID to set
     */
    fun setChatId(chatId: String?) {
        webSocketChatClient?.setChatId(chatId)
    }

    /**
     * Disconnects the WebSocket client.
     */
    fun disconnect() {
        webSocketChatClient?.disconnect()
        webSocketChatClient = null
    }
}