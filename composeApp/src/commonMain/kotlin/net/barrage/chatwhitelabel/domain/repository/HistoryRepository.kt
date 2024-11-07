package net.barrage.chatwhitelabel.domain.repository

import io.ktor.http.Parameters
import net.barrage.chatwhitelabel.domain.Response
import net.barrage.chatwhitelabel.domain.model.History

interface HistoryRepository {
    suspend fun getChats(parameters: Parameters): Response<History>
}
