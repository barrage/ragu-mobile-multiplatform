package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.ui.components.NoInternetAlert
import net.barrage.chatwhitelabel.utils.getAndroidVersion

@Composable
fun Overlays(appState: AppState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (!appState.networkAvailable.value) {
            NoInternetOverlay(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun NoInternetOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        NoInternetAlert(modifier = Modifier.fillMaxWidth().padding(20.dp).align(Alignment.Center))
    }
}

@Composable
fun blurCondition(networkAvailable: State<Boolean>): Modifier {
    return if (!networkAvailable.value) {
        if (getAndroidVersion() > 30 || getAndroidVersion() == -1) {
            Modifier.clickable {}.blur(10.dp)
        } else Modifier.clickable {}.background(Color.Black.copy(alpha = 0.7f))
    } else Modifier
}
