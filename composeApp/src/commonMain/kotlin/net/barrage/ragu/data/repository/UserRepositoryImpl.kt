package net.barrage.ragu.data.repository

import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.mapper.toDomain
import net.barrage.ragu.domain.model.CurrentUser
import net.barrage.ragu.domain.remote.ktor.Api
import net.barrage.ragu.domain.repository.UserRepository

class UserRepositoryImpl(private val api: Api) : UserRepository {
    override suspend fun getCurrentUser(): Response<CurrentUser> {
        return when (val response = api.getCurrentUser()) {
            is Response.Success -> Response.Success(response.data.toDomain())
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}
