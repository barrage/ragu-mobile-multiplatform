package net.barrage.chatwhitelabel.utils

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import net.barrage.chatwhitelabel.ui.screens.chat.ReceiveMessageCallback

class WebSocketChatClient(
    private val receiveMessageCallback: ReceiveMessageCallback,
    private val scope: CoroutineScope,
) {
    private var openChat = false
    private var currentConversationId: String? = null
    private var sendFirstMessage: () -> Unit = {}
    private val userId = "11a8379e-6654-4293-a6b0-cda267c45f8f"

    private var session: WebSocketSession? = null
    private var sessionJob: Job? = null

    init {
        connect()
    }

    private fun connect() {
        val serverUri = "wss://api.tgg-fellow-staging.m2.barrage.beer/?userId=$userId"
        sessionJob =
            scope.launch {
                wsClient.webSocket(serverUri) {
                    receiveMessageCallback.enableSending()
                    session = this
                    handleIncomingMessages(this)
                }
            }
    }

    private suspend fun handleIncomingMessages(wsSession: DefaultClientWebSocketSession) {
        if (wsSession.isActive) {
            for (frame in wsSession.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val message = frame.readText()
                        handleMessage(message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun handleMessage(message: String) {
        if (message == "##STOP##") {
            receiveMessageCallback.stopReceivingMessage()
            return
        }

        runCatching { Json.decodeFromString<ConfigMessage>(message) }
            .onSuccess { jsonMessage ->
                val body = jsonMessage.body as? JsonObject
                val config = body?.get("config")?.let { Json.decodeFromJsonElement<Config>(it) }

                if (config?.stream == true) {
                    currentConversationId = body["chatId"]?.toString()
                    openChat = true
                    sendFirstMessage()
                    sendFirstMessage = {}
                }
            }
            .onFailure { receiveMessageCallback.receiveMessage(message) }
    }

    fun sendMessage(message: String) {
        if (!openChat) {
            openChat(message)
        } else {
            val chatMessageJsonObject = buildJsonObject {
                put("userId", userId)
                put("type", "chat")
                put("payload", message)
            }
            scope.launch { session?.send(Frame.Text(Json.encodeToString(chatMessageJsonObject))) }
        }
    }

    private fun openChat(messageString: String) {
        val language = "eng"

        val openChatMessage =
            ChatMessage(
                userId = userId,
                type = "system",
                payload =
                    buildJsonObject {
                        put("header", "open_chat")
                        put(
                            "body",
                            buildJsonObject {
                                put("type", "open_chat")
                                put("llm", "gpt-4")
                                put("agentId", "1")
                                put("language", language)
                            },
                        )
                    },
            )

        val originalMessageJsonObject = buildJsonObject {
            put("userId", userId)
            put("type", "chat")
            put("payload", messageString)
        }

        scope.launch {
            session?.send(Frame.Text(Json.encodeToString(openChatMessage)))
            sendFirstMessage = {
                scope.launch {
                    session?.send(Frame.Text(Json.encodeToString(originalMessageJsonObject)))
                }
            }
        }
    }

    fun disconnect() {
        scope.launch {
            sessionJob?.cancel()
            receiveMessageCallback.disableSending()
            receiveMessageCallback.stopReceivingMessage()
            session?.close()
            openChat = false
        }
    }

    /*fun cancelMessageStream() {
        val stopStreamMessage = ChatMessage(
            userId = userId,
            type = "system",
            payload = buildJsonObject {
                put("header", "stop_stream")
            }
        )

        scope.launch {
            session?.send(Frame.Text(Json.encodeToString(stopStreamMessage)))
        }
    }*/
}

@Serializable
data class ChatMessage(val userId: String, val type: String, val payload: JsonElement)

@Serializable data class ConfigMessage(val header: String, val body: JsonElement)

@Serializable data class Config(val stream: Boolean, val language: String, val temperature: Double)
