package net.barrage.chatwhitelabel

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import dev.theolm.rinku.compose.ext.Rinku

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            Rinku {
                // Remove when https://issuetracker.google.com/issues/364713509 is fixed
                LaunchedEffect(isSystemInDarkTheme()) {
                    enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
                }
                App()
            }
        }
    }
}

@Preview
@Composable
private fun AppAndroidPreview() {
    App()
}
