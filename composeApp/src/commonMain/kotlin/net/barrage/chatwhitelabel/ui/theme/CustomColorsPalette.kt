package net.barrage.chatwhitelabel.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(val textBase: Color = Color.Unspecified)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }
