package net.barrage.chatwhitelabel.ui.screens.history.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSelectorButton(selectedTheme: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier =
            modifier.fillMaxHeight().aspectRatio(1f).clip(CircleShape).clickable(onClick = onClick)
    ) {
        if (selectedTheme == White) {
            DarkLightElement(
                modifier =
                    Modifier.matchParentSize()
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        } else {
            Box(
                modifier =
                    Modifier.matchParentSize()
                        .background(selectedTheme)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        }
    }
}
