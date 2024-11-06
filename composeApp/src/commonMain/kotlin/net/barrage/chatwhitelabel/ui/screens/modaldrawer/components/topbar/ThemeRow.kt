package net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ThemeRow(
    row: Int,
    supportedThemeColumns: Int,
    supportedThemes: ImmutableList<Color>,
    selectedTheme: Color,
    modifier: Modifier = Modifier,
    onSelectThemeClick: (Color) -> Unit,
) {
    Row(modifier = modifier) {
        for (column in 0 until supportedThemeColumns) {
            val themeColor = supportedThemes.getOrNull(row * supportedThemeColumns + column)
            themeColor?.let {
                ThemeColorBox(
                    color = it,
                    isSelected = (it == selectedTheme),
                    onClick = { onSelectThemeClick(it) },
                )
            }
        }
    }
}
