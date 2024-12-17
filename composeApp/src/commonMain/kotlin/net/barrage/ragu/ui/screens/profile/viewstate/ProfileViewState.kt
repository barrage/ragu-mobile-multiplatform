package net.barrage.ragu.ui.screens.profile.viewstate

import kotlinx.collections.immutable.ImmutableMap
import org.jetbrains.compose.resources.StringResource

data class ProfileViewState(
    val id: String,
    val header: ProfileHeaderViewState,
    val email: String,
    val content: ImmutableMap<StringResource, ProfileContentItem>,
)
