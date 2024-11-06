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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import dev.theolm.rinku.DeepLink
import kotlinx.collections.immutable.persistentListOf
import net.barrage.chatwhitelabel.ui.components.TopBar
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.ModalDrawer
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.ModalDrawerContentViewState
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.ModalDrawerHistoryViewState
import net.barrage.chatwhitelabel.utils.BluePrimary
import net.barrage.chatwhitelabel.utils.BrownPrimary
import net.barrage.chatwhitelabel.utils.GreenPrimary
import net.barrage.chatwhitelabel.utils.LimePrimary
import net.barrage.chatwhitelabel.utils.MagentaPrimary
import net.barrage.chatwhitelabel.utils.OrangePrimary
import net.barrage.chatwhitelabel.utils.RedPrimary
import net.barrage.chatwhitelabel.utils.SagePrimary
import net.barrage.chatwhitelabel.utils.TealPrimary
import net.barrage.chatwhitelabel.utils.VioletPrimary
import net.barrage.chatwhitelabel.utils.YellowPrimary

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
    val viewState by remember {
        mutableStateOf(
            ModalDrawerContentViewState(
                persistentListOf(
                    White,
                    SagePrimary,
                    TealPrimary,
                    BluePrimary,
                    VioletPrimary,
                    LimePrimary,
                    GreenPrimary,
                    YellowPrimary,
                    OrangePrimary,
                    RedPrimary,
                    MagentaPrimary,
                    BrownPrimary,
                ),
                persistentListOf(
                    ModalDrawerHistoryViewState("test", "test1"),
                    ModalDrawerHistoryViewState("test", "test2"),
                    ModalDrawerHistoryViewState("test", "test3"),
                    ModalDrawerHistoryViewState("test", "test4"),
                    ModalDrawerHistoryViewState("test", "test5"),
                ),
            )
        )
    }
    ModalNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            ModalDrawer(
                viewState = viewState,
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
