package net.barrage.chatwhitelabel.ui.screens.profile.viewstate

import org.jetbrains.compose.resources.DrawableResource

data class ProfileContentItem(
    val iconId: DrawableResource,
    val iconDescription: String,
    val value: String,
)
