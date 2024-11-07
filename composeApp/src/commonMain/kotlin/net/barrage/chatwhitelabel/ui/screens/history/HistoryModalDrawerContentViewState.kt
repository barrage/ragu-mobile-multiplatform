package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.domain.model.CurrentUser
import net.barrage.chatwhitelabel.domain.model.History

data class HistoryModalDrawerContentViewState(
    val supportedThemes: ImmutableList<Color>,
    val history: HistoryScreenStates<History>,
    val currentUser: HistoryScreenStates<CurrentUser>,
)
