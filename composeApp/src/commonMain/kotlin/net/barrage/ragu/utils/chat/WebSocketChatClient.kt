package net.barrage.ragu.utils.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.domain.model.WebSocketToken
import net.barrage.ragu.domain.usecase.ws.WebSocketTokenUseCase
import net.barrage.ragu.ui.screens.chat.ReceiveMessageCallback
import net.barrage.ragu.utils.Constants
import net.barrage.ragu.utils.debugLog
import net.barrage.ragu.utils.debugLogError
import net.barrage.ragu.utils.wsClient
import kotlin.time.Duration.Companion.seconds

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
 * @property selectedAgentFlow Currently selected chat agent flow
 */
class WebSocketChatClient(
    private val receiveMessageCallback: ReceiveMessageCallback,
    private val scope: CoroutineScope,
    private val selectedAgentFlow: Flow<Agent?>,
    private val webSocketTokenUseCase: WebSocketTokenUseCase,
    handleChatId: (String?) -> Unit,
) {
    private var wsToken: WebSocketToken? = null

    // Current WebSocket session
    private var session: WebSocketSession? = null

    // Job for managing the connection process
    private var connectionJob: Job? = null

    // Current chat ID, null if no chat is active
    var currentChatId = mutableStateOf<String?>(null)

    // Flag indicating whether a chat is currently open
    private var isChatOpen = mutableStateOf(false)

    // Agent flow collector job
    private var agentFlowJob: Job? = null

    // Flag indicating whether a chat is currently being opened
    private var isOpeningChat = false


    // Modify MessageHandler to handle chat open state
    private val messageHandler = MessageHandler(
        receiveMessageCallback,
        handleChatId = { chatId ->
            handleChatId(chatId)
            if (chatId != null) {
                isOpeningChat = false
            }
        },
        handleChatOpen = { isOpen ->
            isChatOpen.value = isOpen
            if (isOpen) {
                isOpeningChat = false
            }
        },
    )

    // Last message sent, used for retrying failed messages
    private var lastMessage: String? = null

    // Selected agent for the chat session
    var selectedAgent: MutableState<Agent?> = mutableStateOf(null)

    /** Public function to trigger reconnection from outside */
    suspend fun reconnect() {
        connectionJob = coroutineScope {
            launch {
                try {
                    connectWithRetry()
                } catch (e: CancellationException) {
                    debugLog("Reconnection cancelled: ${e.message}")
                } catch (e: Exception) {
                    debugLogError("Reconnection failed", e)
                    reconnect()
                }
            }
        }
    }

    /**
     * Attempts to connect to the WebSocket server with a retry mechanism. Implements an exponential
     * backoff strategy for retries.
     */
    private suspend fun connectWithRetry() = coroutineScope {
        var retryDelay = 1.seconds
        disconnect()
        while (this.isActive) {
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
        var result = false
        webSocketTokenUseCase().collectLatest { response ->
            result = when (response) {
                is Response.Success -> {
                    wsToken = response.data
                    true
                }

                is Response.Loading -> {
                    // Optionally handle loading state
                    false
                }

                else -> {
                    // Handle failure cases
                    debugLogError(
                        "Failed to obtain WebSocket token",
                        (response as? Response.Failure)?.e
                    )
                    false
                }
            }
        }
        return result
    }

    /** Establishes a WebSocket connection to the server. */
    private suspend fun connect() {
        try {
            val serverUri = "wss://${Constants.BASE_URL}/?token=${wsToken?.value}"
            wsClient.webSocket(serverUri) {
                receiveMessageCallback.enableSending()
                session = this
                isChatOpen.value = false
                isOpeningChat = false
                agentFlowJob?.cancel()
                agentFlowJob = launch {
                    selectedAgentFlow.collectLatest { agent ->
                        debugLog("Selected agent changed: $agent")
                        debugLog("Current chat ID: ${currentChatId.value}")
                        debugLog("Is chat open: ${isChatOpen.value}")
                        debugLog("Current agent: ${selectedAgent.value}")
                        agent?.let {
                            if (it.active && !isOpeningChat) {
                                if (selectedAgent.value?.id != agent.id && currentChatId.value == null) {
                                    selectedAgent.value = agent
                                    openNewChat(it)
                                } else if (currentChatId.value != null) {
                                    openExistingChat(currentChatId.value!!)
                                }
                            }
                        }
                    }
                }
                handleIncomingMessages(this)
            }
        } catch (e: Exception) {
            debugLogError("Connection failed", e)
            receiveMessageCallback.disableSending()
            isOpeningChat = false
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
        } catch (e: CancellationException) {
            debugLog("WebSocket cancelled: ${e.message}")
            receiveMessageCallback.disableSending()
        } catch (e: Exception) {
            debugLogError("Error handling incoming messages", e)
            receiveMessageCallback.disableSending()
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
        lastMessage = message
        scope.launch {
            if (!isChatOpen.value) {
                debugLog("Chat is not open, opening a new chat")
                if (currentChatId.value != null) {
                    openExistingChat(currentChatId.value!!)
                } else {
                    selectedAgent.value?.let {
                        openNewChat(it)
                    }
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
    private fun openNewChat(selectedAgent: Agent) {
        if (isOpeningChat) {
            debugLog("Chat opening already in progress, skipping openNewChat")
            return
        }
        debugLog("Opening new chat")
        isOpeningChat = true
        val openChatMessage = buildJsonObject {
            put("type", "system")
            put(
                "payload",
                buildJsonObject {
                    put("type", "chat_open_new")
                    put("agentId", selectedAgent.id)
                },
            )
        }
        sendJsonMessage(openChatMessage)
    }

    /** Opens an existing chat. */
    private fun openExistingChat(chatId: String) {
        if (isOpeningChat) {
            debugLog("Chat opening already in progress, skipping openExistingChat")
            return
        }
        debugLog("Opening existing chat: $chatId")
        isOpeningChat = true
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
    suspend fun disconnect() {
        if (wsClient.isActive && session?.isActive == true) {
            connectionJob?.cancel()
            connectionJob = null

            agentFlowJob?.cancel()
            agentFlowJob = null

            session?.close()
            session = null

            wsClient.close()

            isChatOpen.value = false
            isOpeningChat = false
            receiveMessageCallback.stopReceivingMessage()
            debugLog("WebSocket Disconnected")
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

    /** Sets the current chat ID and agent. */
    fun setChatId(chatId: String?, isNewChat: Boolean) {
        if (chatId == currentChatId.value && isChatOpen.value) {
            return
        }
        if (isOpeningChat) {
            debugLog("Chat opening already in progress, skipping setChatId")
            return
        }
        currentChatId.value = chatId
        isChatOpen.value = false

        if (isNewChat || chatId == null) {
            selectedAgent.value?.let { openNewChat(it) }
        } else {
            openExistingChat(chatId)
        }
    }

    /**
     * Retries the last sent message.
     */
    fun retryLastMessage() {
        debugLog("Retrying last message")
        lastMessage?.let { lastMessage ->
            isChatOpen.value = !isChatOpen.value
            sendMessage(lastMessage)
        }
    }
}
