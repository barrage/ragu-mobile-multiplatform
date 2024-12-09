package net.barrage.chatwhitelabel.data.remote.ktor

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.chatwhitelabel.data.remote.dto.agent.AgentResponse
import net.barrage.chatwhitelabel.data.remote.dto.chat.ChatItemDTO
import net.barrage.chatwhitelabel.data.remote.dto.history.ChatHistoryResponseDTO
import net.barrage.chatwhitelabel.data.remote.dto.history.ChatMessageItemDTO
import net.barrage.chatwhitelabel.data.remote.dto.user.CurrentUserDTO
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.remote.ktor.Api
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

    override suspend fun getHistory(parameters: Parameters): Response<ChatHistoryResponseDTO> {
        return safeApiCall {
            val response =
                httpClient.get("chats") {
                    addCookieHeader()
                    parameters.forEach { key, value -> parameter(key, value) }
                }
            response
        }
    }

    override suspend fun getChatMessagesById(chatId: String): Response<List<ChatMessageItemDTO>> {
        return safeApiCall {
            val response = httpClient.get("chats/$chatId/messages") { addCookieHeader() }
            response
        }
    }

    override suspend fun getWebSocketToken(): Response<String> {
        return safeApiCall {
            val response = httpClient.get("ws") { addCookieHeader() }
            response
        }
    }

    override suspend fun updateChatTitle(chatId: String, title: String): Response<HttpResponse> {
        return safeApiCall {
            val response =
                httpClient.put("chats/$chatId") {
                    addCookieHeader()
                    setBody(mapOf("title" to title))
                }
            response
        }
    }

    override suspend fun deleteChat(chatId: String): Response<HttpResponse> {
        return safeApiCall {
            val response = httpClient.delete("chats/$chatId") { addCookieHeader() }
            response
        }
    }

    override suspend fun getAgents(): Response<AgentResponse> {
        return safeApiCall {
            val response = httpClient.get("agents") { addCookieHeader() }
            response
        }
    }

    override suspend fun evaluateMessage(
        chatId: String,
        messageId: String,
        evaluation: Boolean,
    ): Response<HttpResponse> {
        return safeApiCall {
            val response =
                httpClient.patch("chats/$chatId/messages/$messageId") {
                    addCookieHeader()
                    setBody(mapOf("evaluation" to evaluation))
                }
            response
        }
    }

    override suspend fun getChatById(chatId: String): Response<ChatItemDTO> {
        return safeApiCall {
            val response = httpClient.get("chats/$chatId") { addCookieHeader() }
            response
        }
    }

    private suspend fun HttpRequestBuilder.addCookieHeader() {
        tokenStorage.getCookie()?.let { cookie -> header("Cookie", cookie) }
    }
}
