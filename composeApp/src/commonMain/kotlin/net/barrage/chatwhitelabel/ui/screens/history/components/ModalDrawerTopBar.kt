package net.barrage.chatwhitelabel.ui.screens.history.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import net.barrage.chatwhitelabel.ui.screens.history.HistoryModalDrawerContentViewState
import net.barrage.chatwhitelabel.ui.screens.history.components.topbar.DarkLightThemeSwitcher
import net.barrage.chatwhitelabel.ui.screens.history.components.topbar.ThemePopup
import net.barrage.chatwhitelabel.ui.screens.history.components.topbar.ThemeSelectorButton

@Composable
fun ModalDrawerContentTopBar(
    viewState: HistoryModalDrawerContentViewState,
    theme: Color,
    isDarkMode: Boolean,
    onSelectThemeClick: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onDarkLightModeClick: () -> Unit,
) {
    val supportedThemeColumns = 4
    val themeRows = ceil(viewState.supportedThemes.size.toFloat() / supportedThemeColumns).toInt()
    var showPopup by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DarkLightThemeSwitcher(isDarkMode, onClick = onDarkLightModeClick)

        Column {
            ThemeSelectorButton(selectedTheme = theme, onClick = { showPopup = !showPopup })

            AnimatedVisibility(
                visible = showPopup,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
            ) {
                ThemePopup(
                    themeRows = themeRows,
                    supportedThemeColumns = supportedThemeColumns,
                    supportedThemes = viewState.supportedThemes,
                    selectedTheme = theme,
                    onDismissRequest = { showPopup = false },
                    onSelectThemeClick = {
                        onSelectThemeClick(it)
                        showPopup = false
                    },
                )
            }
        }
    }
}
