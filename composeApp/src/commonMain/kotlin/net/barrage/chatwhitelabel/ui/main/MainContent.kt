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
import dev.theolm.rinku.DeepLink
import kotlinx.coroutines.launch
import net.barrage.chatwhitelabel.ui.components.TopBar
import net.barrage.chatwhitelabel.ui.screens.history.ModalDrawer

@Composable
fun MainContent(
    appState: AppState,
    theme: Color,
    isDarkMode: Boolean,
    deepLink: DeepLink?,
    onSelectThemeClick: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onDarkLightModeClick: () -> Unit,
) {
    val drawerState = appState.drawerState
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            ModalDrawer(
                isDarkMode = isDarkMode,
                theme = theme,
                onSelectThemeClick = onSelectThemeClick,
                onDarkLightModeClick = onDarkLightModeClick,
                drawerState = drawerState,
                onUserClick = { scope.launch { appState.drawerState.close() } },
            )
        },
        drawerState = drawerState,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 20.dp))
            AppNavHost(
                appState = appState,
                deepLink = deepLink,
                modifier = Modifier.weight(1f).padding(bottom = 20.dp),
            )
        }
    }
}
