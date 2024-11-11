package net.barrage.chatwhitelabel.ui.screens.history

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import kotlinx.collections.immutable.ImmutableList
import net.barrage.chatwhitelabel.domain.model.History

data class HistoryModalDrawerContentViewState(
    val supportedThemes: ImmutableList<Color>,
    val supportedVariants: ImmutableList<PaletteStyle>,
    val history: HistoryScreenStates<History>,
)
