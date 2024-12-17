package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.svenjacobs.reveal.Reveal
import com.svenjacobs.reveal.RevealCanvasState
import com.svenjacobs.reveal.rememberRevealState
import dev.theolm.rinku.DeepLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.navigation.Chat
import net.barrage.chatwhitelabel.ui.components.TopBar
import net.barrage.chatwhitelabel.ui.components.reveal.RevealKeys
import net.barrage.chatwhitelabel.ui.components.reveal.RevealOverlayContent
import net.barrage.chatwhitelabel.ui.screens.history.ModalDrawer
import net.barrage.chatwhitelabel.utils.coreComponent

@Composable
fun MainContent(
    appState: AppState,
    currentTheme: Color,
    currentVariant: PaletteStyle,
    isDarkMode: Boolean,
    profileVisible: Boolean,
    deepLink: DeepLink?,
    onSelectThemeClick: (Color) -> Unit,
    onSelectVariantClick: (PaletteStyle) -> Unit,
    onProfileVisibilityChange: () -> Unit,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onDarkLightModeClick: () -> Unit,
    revealCanvasState: RevealCanvasState,
    shouldShowOnboardingTutorial: Boolean,
) {
    val drawerState = appState.drawerState
    val scope = rememberCoroutineScope()
    val revealState = rememberRevealState()
    val shouldShowTutorial = remember { mutableStateOf(shouldShowOnboardingTutorial) }
    val inputEnabled = remember { mutableStateOf(true) }
    Reveal(
        onOverlayClick = { key ->
            appState.coroutineScope.launch {
                revealState.hide()
                when (key as RevealKeys) {
                    RevealKeys.Menu -> {
                        delay(1000)
                        drawerState.open()
                        revealState.reveal(RevealKeys.MenuTheme)
                    }

                    RevealKeys.TitleMenu -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.TitleMenu)
                    }

                    RevealKeys.Account -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.MenuClose)
                    }

                    RevealKeys.AgentItem -> {
                        /**
                         * This is the starting point of the reveal tutorial on app startup (check [net.barrage.chatwhitelabel.ui.components.chat.AgentList])
                         */
                        delay(1000)
                        revealState.reveal(RevealKeys.ChatInput)
                    }

                    RevealKeys.ChatInput -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.Menu)
                    }

                    RevealKeys.MenuClose -> {
                        /**
                         * This is the ending point of the reveal tutorial on app startup
                         */
                        delay(1000)
                        drawerState.close()
                        coreComponent.appPreferences.saveShouldShowOnboardingTutorial(false)
                        shouldShowTutorial.value = false
                        inputEnabled.value = true
                    }

                    RevealKeys.MenuColor -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.MenuNewChat)
                    }

                    RevealKeys.MenuHistory -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.Account)
                    }

                    RevealKeys.MenuTheme -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.MenuColor)
                    }

                    RevealKeys.MenuNewChat -> {
                        delay(1000)
                        revealState.reveal(RevealKeys.MenuHistory)
                    }

                    RevealKeys.ChatTitle -> {
                        delay(1000)
                        revealState.hide()
                        coreComponent.appPreferences.saveShouldShowChatTitleTutorial(false)
                    }
                }
            }
        },
        modifier = modifier,
        revealCanvasState = revealCanvasState,
        revealState = revealState,
        overlayContent = { key -> RevealOverlayContent(key) },
    ) {
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = profileVisible.not() && appState.currentScreen == Chat && inputEnabled.value,
            drawerContent = {
                ModalDrawer(
                    isDarkMode = isDarkMode,
                    currentTheme = currentTheme,
                    currentVariant = currentVariant,
                    onSelectThemeClick = onSelectThemeClick,
                    viewModel = appState.chatViewModel,
                    onSelectVariantClick = onSelectVariantClick,
                    onDarkLightModeClick = onDarkLightModeClick,
                    changeDrawerVisibility = { scope.launch { drawerState.close() } },
                    onUserClick = {
                        onProfileVisibilityChange()
                        scope.launch { drawerState.close() }
                    },
                    revealState = revealState,
                    scope = appState.coroutineScope,
                    modifier = Modifier.fillMaxWidth(0.8f),
                )
            },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (appState.currentScreen == Chat) {
                    TopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        revealState = revealState,
                        scope = appState.coroutineScope,
                        modifier = Modifier.padding(top = 56.dp).padding(horizontal = 10.dp),
                    )
                }
                AppNavHost(
                    appState = appState,
                    deepLink = deepLink,
                    profileVisible = profileVisible,
                    modifier = Modifier.weight(1f).padding(bottom = 20.dp).navigationBarsPadding(),
                    changeProfileVisibility = onProfileVisibilityChange,
                    onLogoutSuccess = onLogoutSuccess,
                    revealCanvasState = revealCanvasState,
                    revealState = revealState,
                    inputEnabled = inputEnabled.value,
                    changeInputEnabled = { inputEnabled.value = it },
                    shouldShowOnboardingTutorial = shouldShowTutorial.value,
                )
            }
        }
    }
}
