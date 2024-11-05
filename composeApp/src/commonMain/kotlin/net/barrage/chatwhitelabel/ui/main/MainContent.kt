package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.theolm.rinku.DeepLink
import net.barrage.chatwhitelabel.ui.components.TopBar

@Composable
fun MainContent(appState: AppState, deepLink: DeepLink?, modifier: Modifier = Modifier) {

    Box(modifier = modifier) {
        Scaffold(
            topBar = {
                TopBar(
                    modifier =
                        Modifier.padding(
                                WindowInsets.safeDrawing
                                    .only(WindowInsetsSides.Top)
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
