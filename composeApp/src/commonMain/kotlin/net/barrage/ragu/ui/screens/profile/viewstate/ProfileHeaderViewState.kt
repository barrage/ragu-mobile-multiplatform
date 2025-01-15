package net.barrage.ragu.ui.screens.profile.viewstate

import androidx.compose.ui.graphics.ImageBitmap

data class ProfileHeaderViewState(
    val profileImage: ImageBitmap?,
    val name: String,
    val active: Boolean
)
