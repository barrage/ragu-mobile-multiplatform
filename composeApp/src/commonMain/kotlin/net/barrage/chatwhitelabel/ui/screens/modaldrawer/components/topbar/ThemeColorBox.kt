package net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp

@Composable
fun ThemeColorBox(
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .padding(1.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.inverseOnSurface else Transparent
                )
                .clickable(onClick = onClick)
                .padding(8.dp)
    ) {
        if (color == White) {
            DarkLightElement(size = 20.dp)
        } else {
            Spacer(modifier = Modifier.clip(CircleShape).background(color).size(20.dp))
        }
    }
}
