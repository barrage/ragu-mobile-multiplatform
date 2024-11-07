package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

data class HistoryModalDrawerContentViewState(
    val supportedThemes: ImmutableList<Color>,
    val history: HistoryScreenState,
)
