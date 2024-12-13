package net.barrage.chatwhitelabel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import com.materialkolor.PaletteStyle
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.rememberRevealCanvasState
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.compose.ext.DeepLinkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.ui.main.MainContent
import net.barrage.chatwhitelabel.ui.main.Overlays
import net.barrage.chatwhitelabel.ui.main.rememberAppState
import net.barrage.chatwhitelabel.ui.theme.CustomTheme
import net.barrage.chatwhitelabel.utils.coreComponent

/**
 * The main composable function for the application.
 * It sets up the app's theme, handles deep links, and manages the main content and overlays.
 *
 * @param modifier Modifier to be applied to the app's root composable.
 * @param onThemeChange Callback function to be invoked when the theme changes. It receives a Boolean
 *                      indicating whether dark mode is enabled.
 */
@Composable
fun App(modifier: Modifier = Modifier, onThemeChange: ((Boolean) -> Unit)? = null) {
    val appState = rememberAppState()
    var deepLink by remember { mutableStateOf<DeepLink?>(null) }
    DeepLinkListener { deepLink = it }
    var isDarkTheme by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf(White) }
    var selectedVariant by remember { mutableStateOf(PaletteStyle.TonalSpot) }
    var isThemeLoaded by remember { mutableStateOf(false) }
    var profileVisible by remember { mutableStateOf(false) }
    val revealCanvasState = rememberRevealCanvasState()

    LaunchedEffect(Unit) {
        isDarkTheme = coreComponent.appPreferences.isDarkModeEnabled()
        selectedTheme = coreComponent.appPreferences.getThemeColor()
        selectedVariant = coreComponent.appPreferences.getThemeVariant()
        isThemeLoaded = true
    }
    LaunchedEffect(isDarkTheme) { onThemeChange?.invoke(isDarkTheme) }
    AnimatedVisibility(isThemeLoaded, enter = fadeIn() + expandVertically()) {
        CustomTheme(
            seedColor = selectedTheme,
            useDarkTheme = isDarkTheme,
            style = selectedVariant,
        ) {
            RevealCanvas(
                modifier = Modifier.fillMaxSize(),
                revealCanvasState = revealCanvasState,
            ) {
                Surface(modifier = modifier) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MainContent(
                            appState = appState,
                            deepLink = deepLink,
                            currentTheme = selectedTheme,
                            currentVariant = selectedVariant,
                            isDarkMode = isDarkTheme,
                            profileVisible = profileVisible,
                            onSelectThemeClick = {
                                selectedTheme = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.saveThemeColor(it)
                                }
                            },
                            onSelectVariantClick = {
                                selectedVariant = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.saveThemeVariant(it)
                                }
                            },
                            onDarkLightModeClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.changeDarkMode(isDarkTheme)
                                }
                                isDarkTheme = !isDarkTheme
                            },
                            onLogoutSuccess = {
                                selectedTheme = White
                                selectedVariant = PaletteStyle.TonalSpot
                                isDarkTheme = false
                                deepLink = null
                                CoroutineScope(Dispatchers.IO).launch {
                                    coreComponent.appPreferences.saveThemeColor(selectedTheme)
                                    coreComponent.appPreferences.saveThemeVariant(selectedVariant)
                                    coreComponent.appPreferences.changeDarkMode(isDarkTheme)
                                }
                            },
                            onProfileVisibilityChange = { profileVisible = !profileVisible },
                            revealCanvasState = revealCanvasState,
                        )
                        Overlays(appState)
                    }
                }
            }
        }
    }
}