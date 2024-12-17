package net.barrage.ragu.data.remote.dto.chat

import kotlinx.serialization.Serializable
import net.barrage.ragu.data.remote.dto.agent.AgentDTO

@Serializable
data class ChatItemDTO(val agent: AgentDTO, val chat: ChatDTO)
