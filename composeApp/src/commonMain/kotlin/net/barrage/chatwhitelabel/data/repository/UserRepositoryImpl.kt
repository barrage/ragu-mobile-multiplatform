package net.barrage.chatwhitelabel.data.repository

import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.mapper.toDomain
import net.barrage.chatwhitelabel.domain.model.CurrentUser
import net.barrage.chatwhitelabel.domain.remote.ktor.Api
import net.barrage.chatwhitelabel.domain.repository.UserRepository

class UserRepositoryImpl(private val api: Api) : UserRepository {
    override suspend fun getCurrentUser(): Response<CurrentUser> {
        return when (val response = api.getCurrentUser()) {
            is Response.Success -> Response.Success(response.data.toDomain())
            is Response.Failure -> response
            is Response.Loading -> Response.Loading
        }
    }
}
