package net.barrage.chatwhitelabel

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.theolm.rinku.compose.ext.Rinku

/**
 * The main activity for the Android application.
 * It sets up the UI and handles the edge-to-edge display and system bar styling.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            Rinku {
                LaunchedEffect(isDarkTheme) {
                    enableEdgeToEdge(
                        statusBarStyle =
                        if (isDarkTheme) {
                            SystemBarStyle.dark(Color.TRANSPARENT)
                        } else {
                            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                        },
                        navigationBarStyle =
                        if (isDarkTheme) {
                            SystemBarStyle.dark(Color.TRANSPARENT)
                        } else {
                            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                        },
                    )
                }
                App(onThemeChange = { darkTheme -> isDarkTheme = darkTheme })
            }
        }
    }
}