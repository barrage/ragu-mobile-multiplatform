package net.barrage.ragu.data.remote.dto.websocket

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface IncomingMessage

@Serializable
sealed class ChatResponse : IncomingMessage {
    @Serializable
    @SerialName("chat.stream_chunk")
    data class StreamChunk(val chunk: String) : ChatResponse()

    @Serializable
    @SerialName("chat.stream_complete")
    data class StreamComplete(
        val chatId: String,
        val reason: String,
        val messageId: String
    ) : ChatResponse()

    @Serializable
    @SerialName("chat.title")
    data class ChatTitle(
        val chatId: String,
        val title: String
    ) : ChatResponse()
}

@Serializable
sealed class SystemResponse : IncomingMessage {
    @Serializable
    @SerialName("system.workflow.open")
    data class WorkflowOpen(val id: String) : SystemResponse()

    @Serializable
    @SerialName("system.workflow.closed")
    data class WorkflowClosed(val id: String) : SystemResponse()

    @Serializable
    @SerialName("system.event.agent_deactivated")
    data class AgentDeactivated(val agentId: String) : SystemResponse()
}

@Serializable
data class ErrorResponse(
    @SerialName("errorType")
    val type: ErrorType,
    @SerialName("errorReason")
    val reason: String,
    @SerialName("errorDescription")
    val description: String? = null
)

@Serializable
enum class ErrorType {
    @SerialName("API")
    API,

    @SerialName("Internal")
    INTERNAL
}