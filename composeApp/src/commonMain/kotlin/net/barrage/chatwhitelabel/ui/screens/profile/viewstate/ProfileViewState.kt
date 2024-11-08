package net.barrage.chatwhitelabel.ui.screens.profile.viewstate

import kotlinx.collections.immutable.ImmutableMap

data class ProfileViewState(
    val id: String,
    val header: ProfileHeaderViewState,
    val content: ImmutableMap<String, ProfileContentItem>,
)
