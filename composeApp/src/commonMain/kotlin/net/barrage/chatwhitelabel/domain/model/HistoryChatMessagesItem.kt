package net.barrage.chatwhitelabel.domain.model

data class HistoryChatMessagesItem(
    val chatId: String,
    val content: String,
    val createdAt: String,
    val id: String,
    val sender: String,
    val senderType: String,
    val updatedAt: String,
)
