package net.barrage.ragu.domain.remote.ktor

import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import net.barrage.ragu.data.remote.dto.agent.AgentResponse
import net.barrage.ragu.data.remote.dto.chat.ChatItemDTO
import net.barrage.ragu.data.remote.dto.history.ChatHistoryResponseDTO
import net.barrage.ragu.data.remote.dto.history.ChatMessageItemDTO
import net.barrage.ragu.data.remote.dto.user.CurrentUserDTO
import net.barrage.ragu.domain.Response

/**
 * Interface defining the API endpoints for the chat application.
 * All methods return a [Response] object wrapping the expected return type.
 */
interface Api {
    /**
     * Performs a login operation.
     * @param parameters The login parameters.
     * @return A Response containing the HttpResponse.
     */
    suspend fun login(parameters: Parameters): Response<HttpResponse>

    /**
     * Performs a logout operation.
     * @return A Response containing the HttpResponse.
     */
    suspend fun logout(): Response<HttpResponse>

    /**
     * Retrieves the current user's information.
     * @return A Response containing the CurrentUserDTO.
     */
    suspend fun getCurrentUser(): Response<CurrentUserDTO>

    /**
     * Retrieves the chat history.
     * @param parameters Query parameters for filtering the history.
     * @return A Response containing the ChatHistoryResponseDTO.
     */
    suspend fun getHistory(parameters: Parameters): Response<ChatHistoryResponseDTO>

    /**
     * Retrieves messages for a specific chat.
     * @param chatId The ID of the chat.
     * @return A Response containing a List of ChatMessageItemDTO.
     */
    suspend fun getChatMessagesById(chatId: String): Response<List<ChatMessageItemDTO>>

    /**
     * Retrieves a WebSocket token.
     * @return A Response containing the token as a String.
     */
    suspend fun getWebSocketToken(): Response<String>

    /**
     * Updates the title of a chat.
     * @param chatId The ID of the chat to update.
     * @param title The new title for the chat.
     * @return A Response containing the HttpResponse.
     */
    suspend fun updateChatTitle(chatId: String, title: String): Response<HttpResponse>

    /**
     * Deletes a chat.
     * @param chatId The ID of the chat to delete.
     * @return A Response containing the HttpResponse.
     */
    suspend fun deleteChat(chatId: String): Response<HttpResponse>

    /**
     * Retrieves a list of agents.
     * @return A Response containing the AgentResponse.
     */
    suspend fun getAgents(): Response<AgentResponse>

    /**
     * Evaluates a message in a chat.
     * @param chatId The ID of the chat containing the message.
     * @param messageId The ID of the message to evaluate.
     * @param evaluation The evaluation value.
     * @return A Response containing the HttpResponse.
     */
    suspend fun evaluateMessage(
        chatId: String,
        messageId: String,
        evaluation: Boolean,
    ): Response<HttpResponse>

    /**
     * Retrieves a specific chat by its ID.
     * @param chatId The ID of the chat to retrieve.
     * @return A Response containing the ChatItemDTO.
     */
    suspend fun getChatById(chatId: String): Response<ChatItemDTO>
}