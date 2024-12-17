package net.barrage.ragu.domain.repository

import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.CurrentUser

interface UserRepository {
    suspend fun getCurrentUser(): Response<CurrentUser>
}
