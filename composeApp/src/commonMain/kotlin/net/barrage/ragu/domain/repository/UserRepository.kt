package net.barrage.ragu.domain.repository

import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.CurrentUser

interface UserRepository {
    suspend fun getCurrentUser(): Flow<Response<CurrentUser>>
}
