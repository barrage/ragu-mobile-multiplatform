package net.barrage.chatwhitelabel.domain.mapper

import kotlinx.collections.immutable.toImmutableList
import net.barrage.chatwhitelabel.data.remote.dto.chat.HistoryElementDTO
import net.barrage.chatwhitelabel.data.remote.dto.chat.HistoryResponseDTO
import net.barrage.chatwhitelabel.domain.model.History
import net.barrage.chatwhitelabel.domain.model.HistoryElement

fun HistoryResponseDTO.toDomain() =
    History(elements = this.items.map { it.toDomain() }.toImmutableList(), itemsNum = this.total)

fun HistoryElementDTO.toDomain() =
    HistoryElement(
        id = this.id,
        agentId = agentId,
        createdAt = createdAt,
        title = title,
        updatedAt = updatedAt,
        userId = userId,
    )
