package net.barrage.ragu.domain.model

import net.barrage.ragu.data.remote.dto.history.SenderType

data class ChatMessageItem(
    val chatId: String? = "",
    val content: String,
    val createdAt: String? = "",
    val id: String? = "",
    val sender: String? = "",
    val senderType: SenderType,
    val updatedAt: String? = "",
)
