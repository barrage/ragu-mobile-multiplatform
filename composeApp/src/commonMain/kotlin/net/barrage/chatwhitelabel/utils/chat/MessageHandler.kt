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

class MessageHandler(private val receiveMessageCallback: ReceiveMessageCallback) {
    var sendFirstMessage: () -> Unit = {}

    fun handleTextFrame(frame: Frame.Text) {
        val message = frame.readText()
        debugLog("WebSocket Incoming: $message")
        if (message.isNotBlank()) {
            handleServerMessage(message)
        } else {
            debugLog("WebSocket Received empty message")
        }
    }

    private fun handleServerMessage(message: String) {
        try {
            val jsonMessage = Json.decodeFromString<JsonObject>(message)
            when (jsonMessage["type"]?.toString()?.trim('"')) {
                "chat_open" -> handleChatOpen(jsonMessage)
                "chat_title" -> handleChatTitle(jsonMessage)
                "chat_closed" -> handleChatClosed()
                "finish_event" -> handleFinishEvent(jsonMessage)
                "error" -> handleError(jsonMessage)
                else -> debugLog("Unhandled message type: ${jsonMessage["type"]}")
            }
        } catch (e: SerializationException) {
            debugLogError("Failed to parse server message as JSON", e.cause)
            handleChatMessageChunk(message)
        }
    }

    private fun handleChatOpen(jsonMessage: JsonObject) {
        val chatId = jsonMessage["chatId"]?.toString()?.trim('"')
        debugLog("WebSocket Chat Opened: $chatId")
        receiveMessageCallback.enableSending()
        sendFirstMessage()
    }

    private fun handleChatTitle(jsonMessage: JsonObject) {
        val title = jsonMessage["title"]?.jsonPrimitive?.content
        if (!title.isNullOrEmpty()) {
            receiveMessageCallback.setChatTitle(title)
        }
    }

    private fun handleChatClosed() {
        receiveMessageCallback.disableSending()
    }

    private fun handleFinishEvent(jsonMessage: JsonObject) {
        val content = jsonMessage["content"]?.toString()?.trim('"')
        content?.let { receiveMessageCallback.receiveMessage(it) }
        receiveMessageCallback.stopReceivingMessage()
    }

    private fun handleError(jsonMessage: JsonObject) {
        val reason = jsonMessage["reason"]?.toString()?.trim('"')
        val description = jsonMessage["description"]?.toString()?.trim('"')
        debugLog("WebSocket Error: $reason - $description")
        receiveMessageCallback.onError("$reason: $description")
    }

    private fun handleChatMessageChunk(chunk: String) {
        if (chunk != "##STOP##") {
            receiveMessageCallback.receiveMessage(chunk)
        } else {
            receiveMessageCallback.stopReceivingMessage()
        }
    }
}
