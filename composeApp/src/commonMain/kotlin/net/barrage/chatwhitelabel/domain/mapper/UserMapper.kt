package net.barrage.chatwhitelabel.domain.mapper

import net.barrage.chatwhitelabel.data.remote.dto.user.CurrentUserDTO
import net.barrage.chatwhitelabel.domain.model.CurrentUser

fun CurrentUserDTO.toDomain() =
    CurrentUser(
        id = id,
        email = email.trim(),
        fullName = fullName.trim(),
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        active = active,
        role = role,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
