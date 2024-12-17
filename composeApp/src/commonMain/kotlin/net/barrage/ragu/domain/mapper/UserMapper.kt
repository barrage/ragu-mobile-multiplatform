package net.barrage.ragu.domain.mapper

import net.barrage.ragu.data.remote.dto.user.CurrentUserDTO
import net.barrage.ragu.domain.model.CurrentUser

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
