package net.barrage.chatwhitelabel.data.remote.dto.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatDTO(
    val agentId: String,
    val createdAt: String,
    val id: String,
    val title: String? = "Chat",
    val updatedAt: String,
    val userId: String,
)
