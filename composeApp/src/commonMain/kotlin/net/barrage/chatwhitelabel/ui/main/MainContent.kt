package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.theolm.rinku.DeepLink
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
    ModalNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            ModalDrawer(
                isDarkMode = isDarkMode,
                theme = theme,
                onSelectThemeClick = onSelectThemeClick,
                onDarkLightModeClick = onDarkLightModeClick,
                onUserClick = {},
            )
        },
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
