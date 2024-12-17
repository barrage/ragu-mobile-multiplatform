package net.barrage.ragu.domain.mapper

import kotlinx.collections.immutable.toImmutableList
import net.barrage.ragu.data.remote.dto.history.ChatHistoryItemDTO
import net.barrage.ragu.data.remote.dto.history.ChatHistoryResponseDTO
import net.barrage.ragu.data.remote.dto.history.ChatMessageItemDTO
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.domain.model.ChatMessageItem

fun ChatHistoryResponseDTO.toDomain() = items.map { it.toDomain() }.toImmutableList()

fun ChatHistoryItemDTO.toDomain() =
    ChatHistoryItem(
        id = id,
        agentId = agentId,
        createdAt = createdAt,
        title = title ?: "Chat",
        updatedAt = updatedAt,
        userId = userId,
    )

fun ChatMessageItemDTO.toDomain() =
    ChatMessageItem(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        chatId = chatId,
        content = content,
        sender = sender,
        senderType = senderType,
    )
