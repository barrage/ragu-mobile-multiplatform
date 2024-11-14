package net.barrage.chatwhitelabel.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.barrage.chatwhitelabel.ui.theme.LocalCustomColorsPalette
import net.barrage.chatwhitelabel.ui.theme.customTypography
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Coming soon...",
            style = customTypography().textBase.fixCenterTextOnAllPlatforms(),
            color = LocalCustomColorsPalette.current.textBase,
        )
    }
}
