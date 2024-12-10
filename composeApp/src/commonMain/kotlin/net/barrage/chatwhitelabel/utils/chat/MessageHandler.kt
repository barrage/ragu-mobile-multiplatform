package net.barrage.chatwhitelabel.utils.chat

import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.barrage.chatwhitelabel.ui.screens.chat.ReceiveMessageCallback
import net.barrage.chatwhitelabel.utils.debugLog
import net.barrage.chatwhitelabel.utils.debugLogError

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
            val jsonMessage = Json.decodeFromString<JsonObject>(message)
            when (jsonMessage["type"]?.toString()?.trim('"')) {
                "chat_open" -> handleChatOpen(jsonMessage)
                "chat_title" -> handleChatTitle(jsonMessage)
                "chat_closed" -> handleChatClosed()
                "finish_event" -> handleFinishEvent()
                "error", "API" -> handleError(jsonMessage)
                else -> debugLog("Unhandled message type: ${jsonMessage["type"]}")
            }
        } catch (e: SerializationException) {
            debugLogError("Failed to parse server message as JSON", e.cause)
            handleChatMessageChunk(message)
        }
    }

    /**
     * Handles the chat open event.
     *
     * @param jsonMessage The JSON message containing chat open information.
     */
    private fun handleChatOpen(jsonMessage: JsonObject) {
        val chatId = jsonMessage["chatId"]?.toString()?.trim('"')
        debugLog("WebSocket Chat Opened: $chatId")
        handleChatId(chatId)
        handleChatOpen(true)
        sendFirstMessage()
    }

    /**
     * Handles the chat title update event.
     *
     * @param jsonMessage The JSON message containing chat title information.
     */
    private fun handleChatTitle(jsonMessage: JsonObject) {
        val title = jsonMessage["title"]?.jsonPrimitive?.content
        val chatId = jsonMessage["chatId"]?.jsonPrimitive?.content
        if (!title.isNullOrEmpty() && !chatId.isNullOrEmpty()) {
            receiveMessageCallback.setChatTitle(title, chatId)
        }
    }

    /** Handles the chat closed event. */
    private fun handleChatClosed() {
        receiveMessageCallback.closeChat()
        handleChatOpen(false)
    }

    /** Handles the finish event. */
    private fun handleFinishEvent() {
        receiveMessageCallback.stopReceivingMessage()
    }

    /**
     * Handles error messages.
     *
     * @param jsonMessage The JSON message containing error information.
     */
    private fun handleError(jsonMessage: JsonObject) {
        val reason = jsonMessage["reason"]?.toString()?.trim('"')
        val description = jsonMessage["description"]?.toString()?.trim('"')
        debugLog("WebSocket Error: $reason - $description")
        receiveMessageCallback.onError("$reason: $description")
    }

    /**
     * Handles incoming chat message chunks.
     *
     * @param chunk The incoming message chunk.
     */
    private fun handleChatMessageChunk(chunk: String) {
        if (chunk != "##STOP##") {
            receiveMessageCallback.receiveMessage(chunk)
        } else {
            receiveMessageCallback.stopReceivingMessage()
        }
    }
}