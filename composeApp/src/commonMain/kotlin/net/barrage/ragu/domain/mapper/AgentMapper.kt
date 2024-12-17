package net.barrage.ragu.domain.mapper

import net.barrage.ragu.data.remote.dto.agent.AgentDTO
import net.barrage.ragu.domain.model.Agent

fun AgentDTO.toDomain() =
    Agent(id = id, active = active, name = name.trim(), description = description?.trim())
