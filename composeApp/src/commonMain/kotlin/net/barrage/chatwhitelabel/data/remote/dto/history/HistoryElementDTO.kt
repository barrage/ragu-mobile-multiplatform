package net.barrage.chatwhitelabel.data.remote.dto.history

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class HistoryElementDTO(
    val id: String,
    val agentId: String,
    val createdAt: Instant,
    val title: String,
    val updatedAt: Instant,
    val userId: String,
)
