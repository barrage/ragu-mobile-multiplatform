package net.barrage.chatwhitelabel.ui.screens.history

import net.barrage.chatwhitelabel.domain.model.History

sealed class HistoryScreenState {
    data object Idle : HistoryScreenState()

    data object Loading : HistoryScreenState()

    data class Success(val data: History) : HistoryScreenState()

    data object Error : HistoryScreenState()
}
