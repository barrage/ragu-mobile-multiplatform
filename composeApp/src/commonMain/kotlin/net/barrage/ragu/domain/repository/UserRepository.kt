package net.barrage.ragu.domain.repository

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.CurrentUser

interface UserRepository {
    suspend fun getCurrentUser(withAvatar: Boolean): Flow<Response<CurrentUser>>
    suspend fun updateAvatar(avatar: ByteArray): Flow<Response<HttpResponse>>
    suspend fun deleteAvatar(): Flow<Response<HttpResponse>>
}
