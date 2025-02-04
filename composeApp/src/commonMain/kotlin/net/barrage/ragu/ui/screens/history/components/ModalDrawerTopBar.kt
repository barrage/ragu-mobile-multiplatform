package net.barrage.ragu.ui.screens.history.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.svenjacobs.reveal.RevealShape
import com.svenjacobs.reveal.RevealState
import com.svenjacobs.reveal.revealable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.reveal.RevealKeys
import net.barrage.ragu.ui.screens.history.HistoryModalDrawerContentViewState
import net.barrage.ragu.ui.screens.history.components.topbar.DarkLightThemeSwitcher
import net.barrage.ragu.ui.screens.history.components.topbar.ThemePopup
import net.barrage.ragu.ui.screens.history.components.topbar.ThemeSelectorButton
import net.barrage.ragu.utils.coreComponent
import kotlin.math.ceil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalDrawerContentTopBar(
    onDarkLightModeClick: () -> Unit,
    onChangeDrawerVisibility: () -> Unit,
    onSelectThemeClick: (Color) -> Unit,
    onSelectVariantClick: (PaletteStyle) -> Unit,
    viewState: HistoryModalDrawerContentViewState,
    currentTheme: Color,
    currentVariant: PaletteStyle,
    isDarkMode: Boolean,
    revealState: RevealState,
    inputEnabled: Boolean,
    shouldShowTutorial: MutableState<Boolean>,
    changeInputEnabled: (Boolean) -> Unit,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    val supportedThemeColumns = 4
    val themeRows = ceil(viewState.supportedThemes.size.toFloat() / supportedThemeColumns).toInt()
    var showPopup by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            IconButton(
                onClick = if (inputEnabled) {
                    onChangeDrawerVisibility
                } else {
                    {}
                }, modifier = Modifier.revealable(
                    key = RevealKeys.MenuClose,
                    shape = RevealShape.Circle,
                    state = revealState,
                    onClick = {
                        scope.launch {
                            revealState.hide()
                            delay(1000)
                            onChangeDrawerVisibility()
                            coreComponent.appPreferences.saveShouldShowOnboardingTutorial(false)
                            shouldShowTutorial.value = false
                            changeInputEnabled(true)
                        }
                    },
                )
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            DarkLightThemeSwitcher(
                isDarkTheme = isDarkMode,
                onClick = if (inputEnabled) {
                    onDarkLightModeClick
                } else {
                    {}
                },
                modifier = Modifier.fillMaxHeight().revealable(
                    key = RevealKeys.MenuTheme,
                    shape = RevealShape.RoundRect(26.dp),
                    state = revealState,
                    onClick = {
                        scope.launch {
                            revealState.hide()
                            delay(1000)
                            revealState.reveal(RevealKeys.MenuColor)
                        }
                    },
                ),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                ThemeSelectorButton(
                    selectedTheme = currentTheme,
                    onClick = {
                        if (inputEnabled) {
                            showPopup = !showPopup
                        }
                    },
                    modifier = Modifier.fillMaxHeight().revealable(
                        key = RevealKeys.MenuColor,
                        shape = RevealShape.Circle,
                        state = revealState,
                        onClick = {
                            scope.launch {
                                revealState.hide()
                                delay(1000)
                                revealState.reveal(RevealKeys.MenuNewChat)
                            }
                        },
                    ),
                )
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
}
