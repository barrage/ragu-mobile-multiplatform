@file:Suppress("TooManyFunctions", "TooGenericExceptionCaught")

package net.barrage.chatwhitelabel.utils.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

/**
 * WebSocketChatClient manages WebSocket connections for chat functionality.
 *
 * This class handles the WebSocket connection to the chat server, including:
 * - Establishing and maintaining the connection
 * - Sending and receiving messages
 * - Managing chat sessions (opening, closing, reconnecting)
 * - Handling connection errors and implementing retry logic
 *
 * @property receiveMessageCallback Callback for handling received messages and connection status
 * @property scope CoroutineScope for managing asynchronous operations
 * @property selectedAgent Currently selected chat agent
 */
class WebSocketChatClient(
    private val receiveMessageCallback: ReceiveMessageCallback,
    private val scope: CoroutineScope,
    private val selectedAgent: MutableState<Agent?>,
) {
    var wsToken: WebSocketToken? = null
        set(value) {
            field = value
            if (connectionJob?.isActive == true) connectionJob?.cancel()
            connectionJob = scope.launch { connectWithRetry() }
        }

    // Current WebSocket session
    var session: WebSocketSession? = null

    // Job for managing the connection process
    private var connectionJob: Job? = null

    // Current chat ID, null if no chat is active
    var currentChatId = mutableStateOf<String?>(null)

    // Flag indicating whether a chat is currently open
    var isChatOpen = mutableStateOf(false)

    // Handler for processing incoming messages
    private val messageHandler =
        MessageHandler(
            receiveMessageCallback,
            handleChatId = { currentChatId.value = it },
            handleChatOpen = { isChatOpen.value = it },
        )

    /**
     * Attempts to connect to the WebSocket server with a retry mechanism. Implements an exponential
     * backoff strategy for retries.
     */
    private suspend fun connectWithRetry() {
        var retryDelay = 1.seconds
        while (true) {
            try {
                connect()
                retryDelay = 1.seconds
                break
            } catch (e: Exception) {
                debugLogError("Connection failed, retrying in $retryDelay", e)
                delay(retryDelay)
                retryDelay = (retryDelay * 2).coerceAtMost(60.seconds)
            }
        }
    }

    /**
     * Establishes a WebSocket connection to the server. Uses a mutex to ensure only one connection
     * attempt at a time.
     */
    private suspend fun connect() {
        try {
            val serverUri = "wss://${Constants.BASE_URL}/?token=${wsToken?.value}"
            wsClient.webSocket(serverUri) {
                receiveMessageCallback.enableSending()
                session = this
                handleIncomingMessages(this)
            }
        } catch (e: Exception) {
            debugLogError("Connection failed", e)
        }
    }

    /**
     * Handles incoming WebSocket messages. Processes different types of frames and manages
     * connection status.
     */
    private suspend fun handleIncomingMessages(wsSession: DefaultClientWebSocketSession) {
        for (frame in wsSession.incoming) {
            when (frame) {
                is Frame.Text -> messageHandler.handleTextFrame(frame)
                is Frame.Close -> debugLog("WebSocket Closed: ${frame.readReason()}")
                else -> debugLog("WebSocket Unsupported frame: ${frame::class.simpleName}")
            }
        }
    }

    /**
     * Sends a chat message to the server. If no chat is open, it will open a new or existing chat
     * before sending the message.
     */
    fun sendMessage(message: String) {
        debugLog("Sending message: $message")
        debugLog("Chat is open: ${isChatOpen.value}")
        debugLog("Current Chat ID: ${currentChatId.value}")
        scope.launch {
            ensureConnected()
            if (!isChatOpen.value) {
                if (currentChatId.value != null) {
                    openExistingChat(currentChatId.value!!)
                } else {
                    openNewChat()
                }
                messageHandler.sendFirstMessage = {
                    sendChatMessage(message)
                    messageHandler.sendFirstMessage = {}
                }
            } else {
                sendChatMessage(message)
            }
        }
    }

    /** Ensures that a WebSocket connection is established before sending messages. */
    private suspend fun ensureConnected() {
        if (session == null || !session!!.isActive) {
            connectWithRetry()
        }
    }

    /** Sends a chat message to the server. */
    private fun sendChatMessage(message: String) {
        val chatMessage = buildJsonObject {
            put("type", "chat")
            put("text", message)
        }
        sendJsonMessage(chatMessage)
    }

    /** Opens a new chat. */
    private fun openNewChat() {
        debugLog("Opening new chat")
        if (currentChatId.value != null) {
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

    /** Opens an existing chat. */
    private fun openExistingChat(chatId: String) {
        debugLog("Opening existing chat: $chatId")
        if (currentChatId.value != chatId) {
            closeChat()
        }
        val openChatMessage = buildJsonObject {
            put("type", "system")
            put(
                "payload",
                buildJsonObject {
                    put("type", "chat_open_existing")
                    put("chatId", chatId)
                },
            )
        }
        sendJsonMessage(openChatMessage)
    }

    /** Sends a JSON message to the server. */
    private fun sendJsonMessage(jsonObject: JsonObject) {
        val messageString = Json.encodeToString(jsonObject)
        debugLog("WebSocket Outgoing: $messageString")
        scope.launch {
            ensureConnected()
            session?.send(Frame.Text(messageString))
        }
    }

    /** Disconnects the WebSocket client and closes the current chat. */
    fun disconnect() {
        scope.launch {
            connectionJob?.cancel()
            session?.close()
            session = null
            isChatOpen.value = false
            debugLog("WebSocket Disconnected")
        }
    }

    /** Closes the current chat session. */
    fun closeChat() {
        if (currentChatId.value != null) {
            debugLog("Closing chat")
            val closeChatMessage = buildJsonObject {
                put("type", "system")
                put("payload", buildJsonObject { put("type", "chat_close") })
            }
            sendJsonMessage(closeChatMessage)
            currentChatId.value = null
            isChatOpen.value = false
        }
    }

    /** Sends a message to stop the current message stream. */
    fun stopMessageStream() {
        val stopStreamMessage = buildJsonObject {
            put("type", "system")
            put("payload", buildJsonObject { put("type", "chat_stop_stream") })
        }
        sendJsonMessage(stopStreamMessage)
    }

    /** Sets the current chat ID. */
    fun setChatId(chatId: String?) {
        debugLog("Set Chat ID: $chatId")
        currentChatId.value = chatId
    }
}
