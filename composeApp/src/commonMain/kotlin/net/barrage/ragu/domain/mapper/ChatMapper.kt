package net.barrage.ragu.domain.mapper

import net.barrage.ragu.data.remote.dto.chat.ChatItemDTO
import net.barrage.ragu.domain.model.ChatItem

fun ChatItemDTO.toDomain() = ChatItem(agent = agent.toDomain())
