package net.barrage.ragu.domain.model

import androidx.compose.ui.graphics.ImageBitmap

data class Agent(
    val active: Boolean,
    val id: String,
    val name: String,
    val description: String? = null,
    val avatarBitmap: ImageBitmap? = null,
)
