package net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSelectorButton(selectedTheme: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (selectedTheme == White) {
        DarkLightElement(
            modifier = modifier.clip(CircleShape).clickable(onClick = onClick),
            size = 24.dp,
        )
    } else {
        Spacer(
            modifier =
                modifier
                    .clip(CircleShape)
                    .background(selectedTheme)
                    .size(24.dp)
                    .clickable(onClick = onClick)
        )
    }
}
