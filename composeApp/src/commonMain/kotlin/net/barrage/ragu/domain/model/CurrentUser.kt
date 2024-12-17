package net.barrage.ragu.domain.model

import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.Instant
import net.barrage.ragu.data.remote.dto.user.Role
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileContentItem
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileHeaderViewState
import net.barrage.ragu.ui.screens.profile.viewstate.ProfileViewState
import net.barrage.ragu.utils.formatIsoDateToReadable
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.created_at
import ragumultiplatform.composeapp.generated.resources.email
import ragumultiplatform.composeapp.generated.resources.ic_created_at
import ragumultiplatform.composeapp.generated.resources.ic_email
import ragumultiplatform.composeapp.generated.resources.ic_role
import ragumultiplatform.composeapp.generated.resources.ic_updated_at
import ragumultiplatform.composeapp.generated.resources.role
import ragumultiplatform.composeapp.generated.resources.updated_at

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
