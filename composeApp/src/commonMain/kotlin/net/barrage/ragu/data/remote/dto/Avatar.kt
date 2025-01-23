package net.barrage.ragu.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Avatar(
    val contentType: String,
    val data: String,
)
