package net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DarkLightElement(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    Row(modifier = modifier.clip(CircleShape).size(size).background(Transparent)) {
        Box(modifier = Modifier.width(size / 2).height(size).background(Black))
        Box(modifier = Modifier.width(size / 2).height(size).background(White))
    }
}
