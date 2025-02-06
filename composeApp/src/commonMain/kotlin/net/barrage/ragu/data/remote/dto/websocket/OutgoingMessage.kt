package net.barrage.ragu.data.remote.dto.websocket

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface OutgoingMessage

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface SystemPayload {
    @SerialName("messageType")
    val type: String
}

@Serializable
@SerialName("workflow.new")
data class NewWorkflowPayload(
    @SerialName("messageType")
    override val type: String = "workflow.new",
    val agentId: String
) : SystemPayload

@Serializable
@SerialName("workflow.existing")
data class ExistingWorkflowPayload(
    @SerialName("messageType")
    override val type: String = "workflow.existing",
    val workflowId: String
) : SystemPayload

@Serializable
@SerialName("workflow.close")
data class CloseWorkflowPayload(
    @SerialName("messageType")
    override val type: String = "workflow.close"
) : SystemPayload

@Serializable
@SerialName("workflow.cancel_stream")
data class CancelStreamPayload(
    @SerialName("messageType")
    override val type: String = "workflow.cancel_stream"
) : SystemPayload

@Serializable
@SerialName("system")
data class SystemMessage(
    @SerialName("messageType")
    val messageType: String = "system",
    val payload: SystemPayload
) : OutgoingMessage

@Serializable
@SerialName("chat")
data class ChatMessage(
    @SerialName("messageType")
    val messageType: String = "chat",
    val text: String
) : OutgoingMessage