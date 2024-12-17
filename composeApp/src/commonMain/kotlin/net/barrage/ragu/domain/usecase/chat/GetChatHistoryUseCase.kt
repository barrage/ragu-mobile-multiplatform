package net.barrage.ragu.domain.usecase.chat

import io.ktor.http.Parameters
import io.ktor.http.parameters
import net.barrage.ragu.domain.repository.HistoryRepository

class GetChatHistoryUseCase(private val historyRepository: HistoryRepository) {
    suspend operator fun invoke(
        page: Int,
        perPage: Int,
        sortBy: String = "updatedAt",
        sortOrder: String = "desc",
    ) = historyRepository.getChats(generateParameters(page, perPage, sortBy, sortOrder))

    private fun generateParameters(
        page: Int,
        perPage: Int,
        sortBy: String,
        sortOrder: String,
    ): Parameters {
        return parameters {
            append("page", page.toString())
            append("perPage", perPage.toString())
            append("sortBy", sortBy)
            append("sortOrder", sortOrder)
        }
    }
}
