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

@Composable
fun App(modifier: Modifier = Modifier) {
    var deepLink by remember { mutableStateOf<DeepLink?>(null) }
    DeepLinkListener { deepLink = it }
    var isDarkTheme by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf(White) }

    LaunchedEffect(Unit) {
        isDarkTheme = coreComponent.appPreferences.isDarkModeEnabled()
        selectedTheme = coreComponent.appPreferences.getThemeColor()
    }
    CustomTheme(seedColor = selectedTheme, useDarkTheme = isDarkTheme) {
        val appState = rememberAppState()

        Surface(modifier = modifier) {
            Box(modifier = Modifier.fillMaxSize()) {
                MainContent(
                    appState = appState,
                    deepLink = deepLink,
                    theme = selectedTheme,
                    isDarkMode = isDarkTheme,
                    onSelectThemeClick = {
                        selectedTheme = it
                        CoroutineScope(Dispatchers.IO).launch {
                            coreComponent.appPreferences.saveThemeColor(it)
                        }
                    },
                    onDarkLightModeClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            coreComponent.appPreferences.changeDarkMode(isDarkTheme)
                        }
                        isDarkTheme = !isDarkTheme
                    },
                )
                Overlays(appState)
            }
        }
    }
}
