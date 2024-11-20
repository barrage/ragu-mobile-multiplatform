package net.barrage.chatwhitelabel.domain.mapper

import net.barrage.chatwhitelabel.data.remote.dto.chat.ChatItemDTO
import net.barrage.chatwhitelabel.domain.model.ChatItem

fun ChatItemDTO.toDomain() = ChatItem(agent = agent.toDomain())
