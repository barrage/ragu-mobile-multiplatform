package net.barrage.ragu.domain.mapper

import com.preat.peekaboo.image.picker.toImageBitmap
import net.barrage.ragu.data.remote.dto.user.CurrentUserDTO
import net.barrage.ragu.domain.model.CurrentUser
import net.barrage.ragu.utils.decodeBase64ToByteArray

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
        avatarBitmap = avatar?.data?.decodeBase64ToByteArray()?.toImageBitmap(),
    )
