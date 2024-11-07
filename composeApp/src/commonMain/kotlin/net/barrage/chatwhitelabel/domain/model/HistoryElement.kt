package net.barrage.chatwhitelabel.domain.model

data class HistoryElement(
    val id: String,
    val agentId: String,
    val createdAt: String,
    val title: String,
    val updatedAt: String,
    val userId: String,
)
