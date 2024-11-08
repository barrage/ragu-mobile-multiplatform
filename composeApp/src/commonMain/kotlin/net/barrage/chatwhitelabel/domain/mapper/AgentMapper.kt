package net.barrage.chatwhitelabel.domain.mapper

import net.barrage.chatwhitelabel.data.remote.dto.agent.AgentDTO
import net.barrage.chatwhitelabel.domain.model.Agent

fun AgentDTO.toDomain() =
    Agent(id = id, active = active, name = name.trim(), description = description?.trim())
