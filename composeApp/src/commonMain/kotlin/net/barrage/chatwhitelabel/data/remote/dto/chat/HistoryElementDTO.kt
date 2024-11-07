package net.barrage.chatwhitelabel.data.remote.dto.chat

import kotlinx.serialization.Serializable

@Serializable
data class HistoryElementDTO(
    val id: String,
    val agentId: String,
    val createdAt: String,
    val title: String,
    val updatedAt: String,
    val userId: String,
)
