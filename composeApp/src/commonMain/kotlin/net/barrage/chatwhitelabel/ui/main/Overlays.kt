package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.background
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
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.no_internet_connection_description
import chatwhitelabel.composeapp.generated.resources.no_internet_connection_title
import net.barrage.chatwhitelabel.ui.components.ErrorDialog
import net.barrage.chatwhitelabel.ui.components.ErrorDialogState
import net.barrage.chatwhitelabel.utils.getAndroidVersion
import org.jetbrains.compose.resources.stringResource

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
    Box(modifier = modifier.fillMaxSize()) {
        ErrorDialog(
            state =
            ErrorDialogState(
                title = stringResource(Res.string.no_internet_connection_title),
                description = stringResource(Res.string.no_internet_connection_description),
                onDismissRequest = {
                    // Do nothing
                },
                confirmButton = {
                    // Do nothing
                },
            ),
            modifier = Modifier.fillMaxWidth().padding(20.dp).align(Alignment.Center),
        )
    }
}

@Composable
fun blurCondition(condition: State<Boolean>): Modifier {
    return if (condition.value) {
        if (getAndroidVersion() > 30 || getAndroidVersion() == -1) {
            Modifier.blur(10.dp)
        } else Modifier.background(Color.Black.copy(alpha = 0.7f))
    } else Modifier
}
