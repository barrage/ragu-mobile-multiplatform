@file:Suppress("TooManyFunctions", "TooGenericExceptionCaught", "TooGenericExceptionThrown")

package net.barrage.chatwhitelabel.utils.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.Agent
import net.barrage.chatwhitelabel.domain.model.WebSocketToken
import net.barrage.chatwhitelabel.domain.usecase.ws.WebSocketTokenUseCase
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
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
) {
    init {
        scope.launch { reconnect() }
    }

    private var wsToken: WebSocketToken? = null

    // Current WebSocket session
    private var session: WebSocketSession? = null

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

    /** Public function to trigger reconnection from outside */
    fun reconnect() {
        scope.launch {
            connectionJob?.cancel()
            connectionJob = launch { connectWithRetry() }
        }
    }

    /**
     * Attempts to connect to the WebSocket server with a retry mechanism. Implements an exponential
     * backoff strategy for retries.
     */
    private suspend fun connectWithRetry() {
        var retryDelay = 1.seconds
        while (true) {
            try {
                if (getWsToken()) {
                    connect()
                    retryDelay = 1.seconds
                    break
                } else {
                    throw Exception("Failed to obtain WebSocket token")
                }
            } catch (e: Exception) {
                debugLogError("Connection failed, retrying in $retryDelay", e)
                delay(retryDelay)
                retryDelay = (retryDelay * 2).coerceAtMost(60.seconds)
            }
        }
    }

    /** Fetches a new WebSocket token */
    private suspend fun getWsToken(): Boolean {
        val response = webSocketTokenUseCase()
        return if (response is Response.Success) {
            wsToken = response.data
            true
        } else {
            false
        }
    }

    /** Establishes a WebSocket connection to the server. */
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
            throw e
        }
    }

    /**
     * Handles incoming WebSocket messages. Processes different types of frames and manages
     * connection status.
     */
    private suspend fun handleIncomingMessages(wsSession: DefaultClientWebSocketSession) {
        try {
            while (true) {
                when (val frame = wsSession.incoming.receive()) {
                    is Frame.Text -> messageHandler.handleTextFrame(frame)
                    is Frame.Close -> {
                        debugLog("WebSocket Closed: ${frame.readReason()}")
                        break
                    }

                    else -> debugLog("WebSocket Unsupported frame: ${frame::class.simpleName}")
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            debugLog("WebSocket Closed: ${e.message}")
            receiveMessageCallback.disableSending()
            reconnect()
        } catch (e: CancellationException) {
            debugLog("WebSocket cancelled: ${e.message}")
            receiveMessageCallback.disableSending()
            reconnect()
        } catch (e: Exception) {
            debugLogError("Error handling incoming messages", e)
            receiveMessageCallback.disableSending()
            reconnect()
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
        scope.launch { session?.send(Frame.Text(messageString)) }
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
