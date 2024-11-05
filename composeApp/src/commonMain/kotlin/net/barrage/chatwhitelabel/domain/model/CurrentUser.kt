package net.barrage.chatwhitelabel.domain.model

data class CurrentUser(
    val id: String,
    val email: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val active: Boolean,
)
