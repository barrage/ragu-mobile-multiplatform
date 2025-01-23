package net.barrage.ragu.domain.mapper

import com.preat.peekaboo.image.picker.toImageBitmap
import net.barrage.ragu.data.remote.dto.agent.AgentDTO
import net.barrage.ragu.domain.model.Agent
import net.barrage.ragu.utils.decodeBase64ToByteArray

fun AgentDTO.toDomain() =
    Agent(
        id = id,
        active = active,
        name = name.trim(),
        description = description?.trim(),
        avatarBitmap = avatar?.data?.decodeBase64ToByteArray()?.toImageBitmap()
    )
