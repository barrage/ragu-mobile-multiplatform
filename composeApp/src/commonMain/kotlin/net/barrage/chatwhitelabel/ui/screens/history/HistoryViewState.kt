package net.barrage.chatwhitelabel.ui.screens.history

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import net.barrage.chatwhitelabel.domain.model.HistoryElement

data class HistoryViewState(
    val elements: ImmutableMap<String?, ImmutableList<HistoryElement>>,
    val itemsNum: Long,
)
