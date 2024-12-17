package net.barrage.ragu.ui.screens.profile.viewstate

import org.jetbrains.compose.resources.DrawableResource

data class ProfileContentItem(
    val iconId: DrawableResource,
    val iconDescription: String,
    val value: String,
)
