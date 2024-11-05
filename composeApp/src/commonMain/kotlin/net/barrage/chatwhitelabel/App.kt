import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.compose.ext.DeepLinkListener
import net.barrage.chatwhitelabel.ui.main.MainContent
import net.barrage.chatwhitelabel.ui.main.Overlays
import net.barrage.chatwhitelabel.ui.main.rememberAppState
import net.barrage.chatwhitelabel.ui.theme.CustomTheme

@Composable
fun App(modifier: Modifier = Modifier) {
    var deepLink by remember { mutableStateOf<DeepLink?>(null) }
    DeepLinkListener { deepLink = it }
    CustomTheme(seedColor = Color(0xFF000080)) {
        val appState = rememberAppState()

        Surface(modifier = modifier) {
            Box(modifier = Modifier.fillMaxSize()) {
                MainContent(appState, deepLink)
                Overlays(appState)
            }
        }
    }
}
