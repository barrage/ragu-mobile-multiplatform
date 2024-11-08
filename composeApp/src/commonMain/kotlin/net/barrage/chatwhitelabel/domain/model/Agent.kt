package net.barrage.chatwhitelabel.domain.model

data class Agent(
    val active: Boolean,
    val id: String,
    val name: String,
    val description: String? = null,
)
