package net.barrage.chatwhitelabel.data.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class HistoryChatMessagesItemDTO(
    val chatId: String,
    val content: String,
    val createdAt: String,
    val evaluation: Boolean? = null,
    val id: String,
    val responseTo: String? = null,
    val sender: String,
    val senderType: String,
    val updatedAt: String,
)
