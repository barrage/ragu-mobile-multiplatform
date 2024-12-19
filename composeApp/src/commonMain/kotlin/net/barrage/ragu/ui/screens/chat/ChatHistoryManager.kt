package net.barrage.ragu.ui.screens.chat

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import net.barrage.ragu.domain.Response
import net.barrage.ragu.domain.model.ChatHistoryItem
import net.barrage.ragu.domain.usecase.chat.ChatUseCase
import net.barrage.ragu.ui.screens.history.HistoryModalDrawerContentViewState
import net.barrage.ragu.ui.screens.history.HistoryScreenStates
import net.barrage.ragu.ui.screens.history.HistoryTimePeriod
import net.barrage.ragu.ui.screens.history.HistoryViewState
import net.barrage.ragu.ui.theme.PaletteVariants
import net.barrage.ragu.ui.theme.ThemeColors
import org.jetbrains.compose.resources.StringResource

/**
 * Manages the chat history functionality.
 *
 * @property chatUseCase Use case for chat-related operations
 */
class ChatHistoryManager(private val chatUseCase: ChatUseCase) {

    private val _historyViewState = MutableStateFlow(
        HistoryModalDrawerContentViewState(
            ThemeColors,
            PaletteVariants,
            HistoryScreenStates.Idle,
        )
    )
    val historyViewState: StateFlow<HistoryModalDrawerContentViewState> =
        _historyViewState.asStateFlow()

    private var currentChatHistoryPage = 1
    private var isLastChatHistoryPage = false
    private val chatHistoryPageSize = 20
    private var isUpdatingHistory = false

    /**
     * Updates the chat history.
     *
     * @param isInitialLoad Whether this is the initial load of the history
     * @param currentChatId The ID of the current chat
     */
    suspend fun updateHistory(isInitialLoad: Boolean = true, currentChatId: String? = null) {
        if (isUpdatingHistory) return
        isUpdatingHistory = true

        if (isInitialLoad) {
            _historyViewState.value =
                historyViewState.value.copy(history = HistoryScreenStates.Loading)
            currentChatHistoryPage = 1
            isLastChatHistoryPage = false
        }

        when (val response =
            chatUseCase.getChatHistory(currentChatHistoryPage, chatHistoryPageSize)) {
            is Response.Success -> {
                val newElements = response.data
                isLastChatHistoryPage = newElements.size < chatHistoryPageSize
                val currentElements = if (isInitialLoad) {
                    emptyList()
                } else {
                    (historyViewState.value.history as? HistoryScreenStates.Success)?.data?.elements?.values?.flatten()
                        ?: emptyList()
                }

                val allElements = (currentElements + newElements).distinctBy { it.id }
                val mappedElements = mapElementsByTimePeriod(allElements, currentChatId)

                _historyViewState.value = historyViewState.value.copy(
                    history = HistoryScreenStates.Success(HistoryViewState(elements = mappedElements))
                )

                currentChatHistoryPage++
            }

            is Response.Failure -> {
                _historyViewState.value =
                    historyViewState.value.copy(history = HistoryScreenStates.Error)
            }

            Response.Loading -> {}
        }
        isUpdatingHistory = false
    }

    /**
     * Loads more history if available.
     *
     * @param currentChatId The ID of the current chat
     */
    suspend fun loadMoreHistory(currentChatId: String?) {
        if (!isLastChatHistoryPage && !isUpdatingHistory) {
            updateHistory(isInitialLoad = false, currentChatId = currentChatId)
        }
    }

    /**
     * Groups chat history elements by time periods and marks the current chat as selected.
     *
     * @param elements List of ChatHistoryItem to be grouped
     * @param currentChatId ID of the current chat, used to mark it as selected
     * @return An ImmutableMap with time periods as keys and lists of ChatHistoryItem as values
     */
    private fun mapElementsByTimePeriod(
        elements: List<ChatHistoryItem>,
        currentChatId: String?,
    ): ImmutableMap<StringResource?, ImmutableList<ChatHistoryItem>> {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val timePeriods = listOf(
            HistoryTimePeriod.TODAY to today,
            HistoryTimePeriod.YESTERDAY to today.minus(1, DateTimeUnit.DAY),
            HistoryTimePeriod.LAST_7_DAYS to today.minus(7, DateTimeUnit.DAY),
            HistoryTimePeriod.LAST_30_DAYS to today.minus(1, DateTimeUnit.MONTH),
            HistoryTimePeriod.LAST_YEAR to today.minus(1, DateTimeUnit.YEAR),
        )

        return elements
            .asSequence()
            .map { it.copy(isSelected = it.id == currentChatId) }
            .groupBy { element ->
                val elementDate =
                    element.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
                timePeriods.firstOrNull { (_, date) -> elementDate >= date }?.first?.label
            }
            .mapValues { (_, value) -> value.toImmutableList() }
            .toImmutableMap()
    }
}