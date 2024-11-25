package net.barrage.chatwhitelabel.ui.screens.history

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import net.barrage.chatwhitelabel.domain.model.ChatHistoryItem

data class HistoryViewState(val elements: ImmutableMap<String?, ImmutableList<ChatHistoryItem>>)
