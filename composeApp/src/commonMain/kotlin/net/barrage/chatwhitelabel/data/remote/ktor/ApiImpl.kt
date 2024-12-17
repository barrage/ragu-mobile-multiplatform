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

/**
 * Implementation of the Api interface using Ktor HTTP client.
 *
 * @property httpClient The Ktor HTTP client used for making API requests.
 * @property tokenStorage The storage mechanism for managing authentication tokens.
 */
class ApiImpl(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) : Api {

    /**
     * Performs a login request.
     *
     * @param parameters The login parameters.
     * @return A Response containing the HttpResponse or an error.
     */
    override suspend fun login(parameters: Parameters): Response<HttpResponse> {
        return safeApiCall {
            httpClient.submitForm(url = "auth/login", formParameters = parameters)
        }
    }

    /**
     * Performs a logout request.
     *
     * @return A Response containing the HttpResponse or an error.
     */
    override suspend fun logout(): Response<HttpResponse> {
        return safeApiCall {
            httpClient.post("auth/logout") { addCookieHeader() }
        }
    }

    /**
     * Retrieves the current user's information.
     *
     * @return A Response containing the CurrentUserDTO or an error.
     */
    override suspend fun getCurrentUser(): Response<CurrentUserDTO> {
        return safeApiCall {
            httpClient.get("users/current") { addCookieHeader() }
        }
    }

    /**
     * Retrieves the chat history.
     *
     * @param parameters Query parameters for the history request.
     * @return A Response containing the ChatHistoryResponseDTO or an error.
     */
    override suspend fun getHistory(parameters: Parameters): Response<ChatHistoryResponseDTO> {
        return safeApiCall {
            httpClient.get("chats") {
                addCookieHeader()
                parameter("perPage", parameters["perPage"]?.toInt() ?: 50)
                parameter("page", parameters["page"]?.toInt() ?: 1)
                parameter("sortBy", parameters["sortBy"] ?: "updatedAt")
                parameter("sortOrder", parameters["sortOrder"] ?: "desc")
            }
        }
    }

    /**
     * Retrieves messages for a specific chat.
     *
     * @param chatId The ID of the chat.
     * @return A Response containing a List of ChatMessageItemDTO or an error.
     */
    override suspend fun getChatMessagesById(chatId: String): Response<List<ChatMessageItemDTO>> {
        return safeApiCall {
            httpClient.get("chats/$chatId/messages") { addCookieHeader() }
        }
    }

    /**
     * Retrieves a WebSocket token.
     *
     * @return A Response containing the token as a String or an error.
     */
    override suspend fun getWebSocketToken(): Response<String> {
        return safeApiCall {
            httpClient.get("ws") { addCookieHeader() }
        }
    }

    /**
     * Updates the title of a chat.
     *
     * @param chatId The ID of the chat to update.
     * @param title The new title for the chat.
     * @return A Response containing the HttpResponse or an error.
     */
    override suspend fun updateChatTitle(chatId: String, title: String): Response<HttpResponse> {
        return safeApiCall {
            httpClient.put("chats/$chatId") {
                addCookieHeader()
                setBody(mapOf("title" to title))
            }
        }
    }

    /**
     * Deletes a chat.
     *
     * @param chatId The ID of the chat to delete.
     * @return A Response containing the HttpResponse or an error.
     */
    override suspend fun deleteChat(chatId: String): Response<HttpResponse> {
        return safeApiCall {
            httpClient.delete("chats/$chatId") { addCookieHeader() }
        }
    }

    /**
     * Retrieves a list of agents.
     *
     * @return A Response containing the AgentResponse or an error.
     */
    override suspend fun getAgents(): Response<AgentResponse> {
        return safeApiCall {
            httpClient.get("agents") { addCookieHeader() }
        }
    }

    /**
     * Evaluates a message in a chat.
     *
     * @param chatId The ID of the chat containing the message.
     * @param messageId The ID of the message to evaluate.
     * @param evaluation The evaluation value.
     * @return A Response containing the HttpResponse or an error.
     */
    override suspend fun evaluateMessage(
        chatId: String,
        messageId: String,
        evaluation: Boolean,
    ): Response<HttpResponse> {
        return safeApiCall {
            httpClient.patch("chats/$chatId/messages/$messageId") {
                addCookieHeader()
                setBody(mapOf("evaluation" to evaluation))
            }
        }
    }

    /**
     * Retrieves a specific chat by its ID.
     *
     * @param chatId The ID of the chat to retrieve.
     * @return A Response containing the ChatItemDTO or an error.
     */
    override suspend fun getChatById(chatId: String): Response<ChatItemDTO> {
        return safeApiCall {
            httpClient.get("chats/$chatId") { addCookieHeader() }
        }
    }

    /**
     * Adds the authentication cookie to the request header.
     */
    private suspend fun HttpRequestBuilder.addCookieHeader() {
        tokenStorage.getCookie()?.let { cookie -> header("Cookie", cookie) }
    }
}