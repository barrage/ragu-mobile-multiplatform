package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
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
                onElementClick = {},
            )
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopBar(
                        modifier =
                            Modifier.padding(
                                    WindowInsets.safeDrawing
                                        .only(WindowInsetsSides.Vertical)
                                        .asPaddingValues()
                                )
                                .padding(horizontal = 20.dp)
                    )
                }
            ) { innerPadding ->
                AppNavHost(
                    appState = appState,
                    deepLink = deepLink,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
