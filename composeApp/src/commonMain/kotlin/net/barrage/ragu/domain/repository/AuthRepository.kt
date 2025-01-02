package net.barrage.ragu.domain.repository

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.AuthToken

interface AuthRepository {
    suspend fun login(parameters: Parameters): Flow<Response<AuthToken>>

    suspend fun logout(): Flow<Response<HttpResponse>>
}
