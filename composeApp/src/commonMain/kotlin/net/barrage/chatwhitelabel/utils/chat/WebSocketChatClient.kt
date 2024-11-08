package net.barrage.chatwhitelabel.utils.chat

import androidx.compose.runtime.MutableState
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSocketException
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.model.WebSocketToken
import net.barrage.chatwhitelabel.ui.screens.chat.ReceiveMessageCallback
import net.barrage.chatwhitelabel.utils.Constants
import net.barrage.chatwhitelabel.utils.debugLog
import net.barrage.chatwhitelabel.utils.debugLogError
import net.barrage.chatwhitelabel.utils.wsClient

class WebSocketChatClient(
    private val receiveMessageCallback: ReceiveMessageCallback,
    private val scope: CoroutineScope,
    private val wsToken: WebSocketToken,
    private val selectedAgent: MutableState<Agent?>,
) {
    var currentChatId: String? = null
    private var session: WebSocketSession? = null
    private val messageHandler = MessageHandler(receiveMessageCallback) { currentChatId = it }

    init {
        connect()
    }

    private fun connect() {
        val serverUri = "wss://${Constants.BASE_URL}/?token=${wsToken.value}"
        scope.launch {
            try {
                wsClient.webSocket(serverUri) {
                    receiveMessageCallback.enableSending()
                    session = this
                    handleIncomingMessages(this)
                }
            } catch (e: WebSocketException) {
                debugLogError("WebSocket Connection failed", e)
            } catch (e: IOException) {
                debugLogError("Network error during WebSocket connection", e)
            }
        }
    }

    private suspend fun handleIncomingMessages(wsSession: DefaultClientWebSocketSession) {
        try {
            for (frame in wsSession.incoming) {
                when (frame) {
                    is Frame.Text -> messageHandler.handleTextFrame(frame)
                    is Frame.Close -> debugLog("WebSocket Closed: ${frame.readReason()}")
                    else -> debugLog("WebSocket Unsupported frame: ${frame::class.simpleName}")
                }
            }
        } catch (e: WebSocketException) {
            debugLogError("WebSocket Error", e)
        } catch (e: IOException) {
            debugLogError("Network Error", e)
        } finally {
            debugLog("WebSocket Connection closed")
        }
    }

    fun sendMessage(message: String) {
        if (currentChatId == null) {
            openNewChat()
            messageHandler.sendFirstMessage = {
                sendChatMessage(message)
                messageHandler.sendFirstMessage = {}
            }
        } else {
            sendChatMessage(message)
        }
    }

    private fun sendChatMessage(message: String) {
        val chatMessage = buildJsonObject {
            put("type", "chat")
            put("text", message)
        }
        sendJsonMessage(chatMessage)
    }

    private fun openNewChat() {
        if (currentChatId != null) {
            closeChat()
        }
        val openChatMessage = buildJsonObject {
            put("type", "system")
            put(
                "payload",
                buildJsonObject {
                    put("type", "chat_open_new")
                    put(
                        "agentId",
                        selectedAgent.value?.id ?: "00000000-0000-0000-0000-000000000000",
                    )
                },
            )
        }
        sendJsonMessage(openChatMessage)
    }

    private fun sendJsonMessage(jsonObject: JsonObject) {
        val messageString = Json.encodeToString(jsonObject)
        debugLog("WebSocket Outgoing: $messageString")
        scope.launch { session?.send(Frame.Text(messageString)) }
    }

    fun disconnect() {
        scope.launch {
            closeChat()
            session?.close()
            currentChatId = null
            debugLog("WebSocket Disconnected")
            receiveMessageCallback.setChatOpen(false)
        }
    }

    fun closeChat() {
        if (currentChatId != null) {
            val closeChatMessage = buildJsonObject {
                put("type", "system")
                put("payload", buildJsonObject { put("type", "chat_close") })
            }
            sendJsonMessage(closeChatMessage)
            currentChatId = null
            receiveMessageCallback.setChatOpen(false)
        }
    }

    fun stopMessageStream() {
        val stopStreamMessage = buildJsonObject {
            put("type", "system")
            put("payload", buildJsonObject { put("type", "chat_stop_stream") })
        }
        sendJsonMessage(stopStreamMessage)
    }
}
