package net.barrage.chatwhitelabel.data.remote.dto.user

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserDTO(
    val id: String,
    val email: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val active: Boolean,
    val role: Role,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Serializable
enum class Role {
    @SerialName("admin") ADMIN,
    @SerialName("user") USER,
    // Add other roles as needed
}
