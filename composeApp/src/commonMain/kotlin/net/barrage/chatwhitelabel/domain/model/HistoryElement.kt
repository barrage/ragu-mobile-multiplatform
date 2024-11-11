package net.barrage.chatwhitelabel.domain.model

import kotlinx.datetime.Instant

data class HistoryElement(
    val id: String,
    val agentId: String,
    val createdAt: Instant,
    val title: String,
    val updatedAt: Instant,
    val userId: String,
    val isSelected: Boolean = false,
)
