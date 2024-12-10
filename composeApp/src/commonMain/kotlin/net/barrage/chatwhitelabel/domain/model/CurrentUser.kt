package net.barrage.chatwhitelabel.domain.model

import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.created_at
import chatwhitelabel.composeapp.generated.resources.email
import chatwhitelabel.composeapp.generated.resources.ic_created_at
import chatwhitelabel.composeapp.generated.resources.ic_email
import chatwhitelabel.composeapp.generated.resources.ic_role
import chatwhitelabel.composeapp.generated.resources.ic_updated_at
import chatwhitelabel.composeapp.generated.resources.role
import chatwhitelabel.composeapp.generated.resources.updated_at
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.Instant
import net.barrage.chatwhitelabel.data.remote.dto.user.Role
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileContentItem
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileHeaderViewState
import net.barrage.chatwhitelabel.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.chatwhitelabel.utils.formatIsoDateToReadable

data class CurrentUser(
    val id: String,
    val email: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val active: Boolean,
    val role: Role,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun toViewState(): ProfileViewState =
        ProfileViewState(
            id = id,
            header = ProfileHeaderViewState(profileImage = "", name = fullName, active = active),
            email = email,
            content =
            persistentMapOf(
                Res.string.email to
                        ProfileContentItem(
                            value = email,
                            iconId = Res.drawable.ic_email,
                            iconDescription = "email",
                        ),
                Res.string.role to
                        ProfileContentItem(
                            value = role.name,
                            iconId = Res.drawable.ic_role,
                            iconDescription = "Role",
                        ),
                Res.string.created_at to
                        ProfileContentItem(
                            value = formatIsoDateToReadable(createdAt),
                            iconId = Res.drawable.ic_created_at,
                            iconDescription = "created At",
                        ),
                Res.string.updated_at to
                        ProfileContentItem(
                            value = formatIsoDateToReadable(createdAt),
                            iconId = Res.drawable.ic_updated_at,
                            iconDescription = "Updated At",
                        ),
            ),
        )
}
