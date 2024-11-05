package net.barrage.chatwhitelabel.data.remote.ktor

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.chatwhitelabel.data.remote.dto.user.CurrentUserDTO
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.utils.TokenStorage
import net.barrage.chatwhitelabel.utils.safeApiCall

class ApiImpl(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) : Api {
    override suspend fun login(parameters: Parameters): Response<HttpResponse> {
        return safeApiCall {
            val response: HttpResponse =
                httpClient.submitForm(url = "auth/login", formParameters = parameters)
            response
        }
    }

    override suspend fun logout(): Response<HttpResponse> {
        return safeApiCall {
            val response = httpClient.post("auth/logout") { addCookieHeader() }
            response
        }
    }

    override suspend fun getCurrentUser(): Response<CurrentUserDTO> {
        return safeApiCall {
            val response = httpClient.get("users/current") { addCookieHeader() }
            response
        }
    }

    override suspend fun getWebSocketToken(): Response<String> {
        return safeApiCall {
            val response = httpClient.get("ws") { addCookieHeader() }
            response
        }
    }

    private suspend fun HttpRequestBuilder.addCookieHeader() {
        tokenStorage.getCookie()?.let { cookie -> header("Cookie", cookie) }
    }
}
