package net.barrage.ragu.data.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable
data class ChatHistoryResponseDTO(val items: List<ChatHistoryItemDTO>, val total: Long)
