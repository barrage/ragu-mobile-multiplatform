package net.barrage.chatwhitelabel.ui.screens.history.components.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ThemePopup(
    themeRows: Int,
    supportedThemeColumns: Int,
    supportedThemes: ImmutableList<Color>,
    selectedTheme: Color,
    onDismissRequest: () -> Unit,
    onSelectThemeClick: (Color) -> Unit,
) {
    Popup(alignment = Alignment.TopCenter, onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.padding(end = 16.dp).wrapContentSize().padding(8.dp)) {
            Column(modifier = Modifier.padding(10.dp).widthIn(max = 300.dp)) {
                for (row in 0 until themeRows) {
                    ThemeRow(
                        row = row,
                        supportedThemeColumns = supportedThemeColumns,
                        supportedThemes = supportedThemes,
                        selectedTheme = selectedTheme,
                        onSelectThemeClick = onSelectThemeClick,
                    )
                }
            }
        }
    }
}
