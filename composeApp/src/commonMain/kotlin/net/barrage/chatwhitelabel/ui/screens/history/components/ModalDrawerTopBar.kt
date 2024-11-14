package net.barrage.chatwhitelabel.ui.screens.history.components

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
import com.materialkolor.PaletteStyle
import kotlin.math.ceil
import net.barrage.chatwhitelabel.ui.screens.history.HistoryModalDrawerContentViewState
import net.barrage.chatwhitelabel.ui.screens.history.components.topbar.DarkLightThemeSwitcher
import net.barrage.chatwhitelabel.ui.screens.history.components.topbar.ThemePopup
import net.barrage.chatwhitelabel.ui.screens.history.components.topbar.ThemeSelectorButton

@Composable
fun ModalDrawerContentTopBar(
    viewState: HistoryModalDrawerContentViewState,
    currentTheme: Color,
    currentVariant: PaletteStyle,
    isDarkMode: Boolean,
    onSelectThemeClick: (Color) -> Unit,
    onSelectVariantClick: (PaletteStyle) -> Unit,
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
            ThemeSelectorButton(selectedTheme = currentTheme, onClick = { showPopup = !showPopup })
            ThemePopup(
                themeRows = themeRows,
                showPopup = showPopup,
                supportedThemeColumns = supportedThemeColumns,
                supportedThemes = viewState.supportedThemes,
                supportedVariants = viewState.supportedVariants,
                selectedVariant = currentVariant,
                selectedTheme = currentTheme,
                onDismissRequest = { showPopup = false },
                onSelectVariantClick = onSelectVariantClick,
                onSelectThemeClick = onSelectThemeClick,
            )
        }
    }
}
