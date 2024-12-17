package net.barrage.ragu.data.remote.dto.history

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatHistoryItemDTO(
    val id: String,
    val agentId: String,
    val createdAt: Instant,
    val title: String? = null,
    val updatedAt: Instant,
    val userId: String,
)
