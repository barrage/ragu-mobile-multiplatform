package net.barrage.chatwhitelabel.domain.model

import net.barrage.chatwhitelabel.data.remote.dto.history.SenderType

data class ChatMessageItem(
    val chatId: String? = "",
    val content: String,
    val createdAt: String? = "",
    val id: String? = "",
    val sender: String? = "",
    val senderType: SenderType,
    val updatedAt: String? = "",
)
