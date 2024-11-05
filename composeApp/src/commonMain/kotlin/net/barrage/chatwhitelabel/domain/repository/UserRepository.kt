package net.barrage.chatwhitelabel.domain.repository

import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.CurrentUser

interface UserRepository {
    suspend fun getCurrentUser(): Response<CurrentUser>
}
