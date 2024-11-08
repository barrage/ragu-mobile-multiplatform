package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    var profileVisible by remember { mutableStateOf(false) }
    val drawerState = appState.drawerState
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = profileVisible.not(),
        drawerContent = {
            ModalDrawer(
                isDarkMode = isDarkMode,
                theme = theme,
                onSelectThemeClick = onSelectThemeClick,
                onDarkLightModeClick = onDarkLightModeClick,
                onUserClick = {
                    profileVisible = true
                    scope.launch { drawerState.close() }
                },
            )
        },
        drawerState = drawerState,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 20.dp))
            AppNavHost(
                appState = appState,
                deepLink = deepLink,
                profileVisible = profileVisible,
                modifier = Modifier.weight(1f).padding(bottom = 20.dp),
                changeProfileVisibility = { profileVisible = !profileVisible },
            )
        }
    }
}
