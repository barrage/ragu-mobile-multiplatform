package net.barrage.chatwhitelabel.data.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class HistoryChatMessagesItemDTO(
    val chatId: String,
    val content: String,
    val createdAt: String,
    //    val evaluation: Any,
    val id: String,
    //    val responseTo: Any,
    val sender: String,
    val senderType: String,
    val updatedAt: String,
)
