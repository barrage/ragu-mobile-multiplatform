package net.barrage.chatwhitelabel.data.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class HistoryElementDTO(
    val id: String,
    val agentId: String,
    val createdAt: String,
    val title: String? = null,
    val updatedAt: String,
    val userId: String,
)
