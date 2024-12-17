package net.barrage.ragu.ui.screens.history

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import net.barrage.ragu.domain.model.ChatHistoryItem
import org.jetbrains.compose.resources.StringResource

data class HistoryViewState(
    val elements: ImmutableMap<StringResource?, ImmutableList<ChatHistoryItem>>
)
