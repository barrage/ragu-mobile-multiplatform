package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import dev.theolm.rinku.DeepLink
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.navigation.Chat
import net.barrage.chatwhitelabel.ui.components.TopBar
import net.barrage.chatwhitelabel.ui.screens.history.ModalDrawer

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
) {
    val drawerState = appState.drawerState
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = profileVisible.not() && appState.currentScreen == Chat,
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
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 20.dp))
            AppNavHost(
                appState = appState,
                deepLink = deepLink,
                profileVisible = profileVisible,
                modifier = Modifier.weight(1f).padding(bottom = 20.dp),
                changeProfileVisibility = onProfileVisibilityChange,
                onLogoutSuccess = onLogoutSuccess,
            )
        }
    }
}
