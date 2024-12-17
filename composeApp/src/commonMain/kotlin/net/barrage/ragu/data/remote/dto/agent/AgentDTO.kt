package net.barrage.ragu.data.remote.dto.agent

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(val total: Int, val items: List<AgentDTO>)

@Serializable
data class AgentDTO(
    val active: Boolean,
    val createdAt: Instant,
    val description: String? = null,
    val id: String,
    val language: String? = null,
    val name: String,
    val updatedAt: Instant,
)
