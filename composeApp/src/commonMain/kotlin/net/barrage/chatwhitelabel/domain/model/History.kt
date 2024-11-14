package net.barrage.chatwhitelabel.domain.model

import kotlinx.collections.immutable.ImmutableList

data class History(val elements: ImmutableList<HistoryElement>, val itemsNum: Long)
