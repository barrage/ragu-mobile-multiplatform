package net.barrage.ragu.utils.chat

import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.barrage.ragu.data.remote.dto.websocket.ChatResponse
import net.barrage.ragu.data.remote.dto.websocket.ErrorResponse
import net.barrage.ragu.data.remote.dto.websocket.IncomingMessage
import net.barrage.ragu.data.remote.dto.websocket.SystemResponse
import net.barrage.ragu.ui.screens.chat.ReceiveMessageCallback
import net.barrage.ragu.utils.debugLog
import net.barrage.ragu.utils.debugLogError

/**
 * Handles incoming WebSocket messages for chat functionality.
 *
 * @property receiveMessageCallback Callback for handling received messages and chat events.
 * @property handleChatId Callback for handling chat ID updates.
 * @property handleChatOpen Callback for handling chat open/close status.
 */
class MessageHandler(
    private val receiveMessageCallback: ReceiveMessageCallback,
    private val handleChatId: (String?) -> Unit,
    private val handleChatOpen: (Boolean) -> Unit,
) {
    /** Function to send the first message when a chat is opened. */
    var sendFirstMessage: () -> Unit = {}

    /** Previous error message for handling retries. */
    private var previousErrorDescription: String? = null

    /**
     * Handles incoming WebSocket text frames.
     *
     * @param frame The incoming WebSocket text frame.
     */
    fun handleTextFrame(frame: Frame.Text) {
        val message = frame.readText()
        debugLog("WebSocket Incoming: $message")
        if (message.isNotEmpty()) {
            handleServerMessage(message)
        } else {
            debugLog("WebSocket Received empty message")
        }
    }

    /**
     * Processes the server message based on its type.
     *
     * @param message The incoming server message as a string.
     */
    private fun handleServerMessage(message: String) {
        try {
            when (val parsed = Json.decodeFromString<IncomingMessage>(message)) {
                is ChatResponse.StreamChunk -> handleChatMessageChunk(parsed.chunk)
                is ChatResponse.ChatTitle -> {
                    receiveMessageCallback.setChatTitle(parsed.title, parsed.chatId)
                    handleChatId(parsed.chatId)
                }

                is ChatResponse.StreamComplete -> {
                    receiveMessageCallback.stopReceivingMessage(parsed.messageId)
                    handleChatId(parsed.chatId)
                }

                is SystemResponse.WorkflowOpen -> {
                    debugLog("WebSocket Chat Opened: ${parsed.id}")
                    handleChatOpen(true)
                    handleChatId(parsed.id)
                    sendFirstMessage()
                }

                is SystemResponse.WorkflowClosed -> {
                    receiveMessageCallback.closeChat()
                    handleChatOpen(false)
                }

                is SystemResponse.AgentDeactivated -> {
                    receiveMessageCallback.agentDeactivated(parsed.agentId)
                }
            }
        } catch (e: SerializationException) {
            try {
                val parsed = Json.decodeFromString<ErrorResponse>(message)
                receiveMessageCallback.onError(
                    "${parsed.reason}: ${parsed.description}",
                    retry = previousErrorDescription != parsed.description
                )
                previousErrorDescription = parsed.description
                debugLog("WebSocket Error: ${parsed.reason} ${parsed.description}")
            } catch (e: Exception) {
                debugLogError("Failed to parse server message as JSON", e.cause)
            }
        }
    }

    /**
     * Handles incoming chat message chunks.
     *
     * @param chunk The incoming message chunk.
     */
    private fun handleChatMessageChunk(chunk: String) {
        receiveMessageCallback.receiveMessage(chunk)
    }
}