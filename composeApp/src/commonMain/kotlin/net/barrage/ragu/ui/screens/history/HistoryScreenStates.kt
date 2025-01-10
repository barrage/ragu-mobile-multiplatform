package net.barrage.ragu.ui.screens.history

sealed class HistoryScreenStates<out T> {
    data object Idle : HistoryScreenStates<Nothing>()

    data object Loading : HistoryScreenStates<Nothing>()

    data class Success<out T>(val data: T) : HistoryScreenStates<T>()

    data object Error : HistoryScreenStates<Nothing>()
    data object Unauthorized : HistoryScreenStates<Nothing>()
}
