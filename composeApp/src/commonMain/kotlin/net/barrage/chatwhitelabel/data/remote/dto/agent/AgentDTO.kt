package net.barrage.chatwhitelabel.data.remote.dto.agent

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable data class AgentResponse(val total: Int, val items: List<AgentDTO>)

@Serializable
data class AgentDTO(
    val active: Boolean,
    val context: String,
    val createdAt: Instant,
    val description: String?,
    val embeddingModel: String,
    val embeddingProvider: String,
    val id: String,
    val language: String,
    val llmProvider: String,
    val model: String,
    val name: String,
    val temperature: Double,
    val updatedAt: Instant,
    val vectorProvider: String,
)
