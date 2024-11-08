package net.barrage.chatwhitelabel.domain.mapper

import kotlinx.collections.immutable.toImmutableList
import net.barrage.chatwhitelabel.data.remote.dto.history.HistoryChatMessagesItemDTO
import net.barrage.chatwhitelabel.data.remote.dto.history.HistoryElementDTO
import net.barrage.chatwhitelabel.data.remote.dto.history.HistoryResponseDTO
import net.barrage.chatwhitelabel.domain.model.History
import net.barrage.chatwhitelabel.domain.model.HistoryChatMessagesItem
import net.barrage.chatwhitelabel.domain.model.HistoryElement

fun HistoryResponseDTO.toDomain() =
    History(elements = items.map { it.toDomain() }.toImmutableList(), itemsNum = this.total)

fun HistoryElementDTO.toDomain() =
    HistoryElement(
        id = id,
        agentId = agentId,
        createdAt = createdAt,
        title = title ?: "Chat",
        updatedAt = updatedAt,
        userId = userId,
    )

fun HistoryChatMessagesItemDTO.toDomain() =
    HistoryChatMessagesItem(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        chatId = chatId,
        content = content,
        sender = sender,
        senderType = senderType,
    )
