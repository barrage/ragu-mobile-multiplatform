package net.barrage.chatwhitelabel.data.remote.dto.chat

import kotlinx.serialization.Serializable
import net.barrage.chatwhitelabel.data.remote.dto.agent.AgentDTO

@Serializable data class ChatItemDTO(val agent: AgentDTO, val chat: ChatDTO)
