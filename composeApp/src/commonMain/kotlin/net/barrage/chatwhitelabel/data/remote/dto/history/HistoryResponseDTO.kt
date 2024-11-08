package net.barrage.chatwhitelabel.data.remote.dto.history

import kotlinx.serialization.Serializable

@Serializable data class HistoryResponseDTO(val items: List<HistoryElementDTO>, val total: Long)
