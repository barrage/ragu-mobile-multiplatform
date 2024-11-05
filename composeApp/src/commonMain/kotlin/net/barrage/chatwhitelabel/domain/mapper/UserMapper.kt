package net.barrage.chatwhitelabel.domain.mapper

import net.barrage.chatwhitelabel.data.remote.dto.user.CurrentUserDTO
import net.barrage.chatwhitelabel.domain.model.CurrentUser

fun CurrentUserDTO.toDomain() =
    CurrentUser(
        id = id,
        email = email,
        fullName = fullName,
        firstName = firstName,
        lastName = lastName,
        active = active,
    )
