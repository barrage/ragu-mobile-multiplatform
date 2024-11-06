package net.barrage.chatwhitelabel.ui.screens.modaldrawer

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.ModalDrawerHistoryViewState

data class ModalDrawerContentViewState(
    val supportedThemes: ImmutableList<Color>,
    val history: ImmutableList<ModalDrawerHistoryViewState>,
)
