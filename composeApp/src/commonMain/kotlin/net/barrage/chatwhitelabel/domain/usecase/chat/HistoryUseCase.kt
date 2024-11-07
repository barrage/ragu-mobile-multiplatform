package net.barrage.chatwhitelabel.domain.usecase.chat

import io.ktor.http.Parameters
import io.ktor.http.parameters
import net.barrage.chatwhitelabel.domain.repository.HistoryRepository

class HistoryUseCase(private val historyRepository: HistoryRepository) {
    suspend fun invoke(
        page: Int,
        perPage: Int,
        sortBy: String = "name",
        sortOrder: String = "asc",
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
