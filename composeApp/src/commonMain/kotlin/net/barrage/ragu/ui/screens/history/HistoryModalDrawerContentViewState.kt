package net.barrage.ragu.ui.screens.history

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import kotlinx.collections.immutable.ImmutableList

data class HistoryModalDrawerContentViewState(
    val supportedThemes: ImmutableList<Color>,
    val supportedVariants: ImmutableList<PaletteStyle>,
    val history: HistoryScreenStates<HistoryViewState>,
)
