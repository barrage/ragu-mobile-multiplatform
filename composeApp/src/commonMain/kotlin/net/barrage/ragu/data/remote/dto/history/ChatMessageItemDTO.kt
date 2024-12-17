package net.barrage.ragu.data.remote.dto.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageItemDTO(
    val chatId: String,
    val content: String,
    val createdAt: String,
    val evaluation: Boolean? = null,
    val id: String,
    val responseTo: String? = null,
    val sender: String,
    val senderType: SenderType,
    val updatedAt: String,
)

@Serializable
enum class SenderType {
    @SerialName("assistant")
    ASSISTANT,

    @SerialName("user")
    USER,

    @SerialName("error")
    ERROR,
    // Add other roles as needed
}
