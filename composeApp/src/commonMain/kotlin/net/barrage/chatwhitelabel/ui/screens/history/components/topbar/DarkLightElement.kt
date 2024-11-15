package net.barrage.chatwhitelabel.ui.screens.history.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun DarkLightElement(modifier: Modifier = Modifier, size: Dp? = null) {
    if (size == null) {
        Row(modifier = modifier.clip(CircleShape).background(Transparent)) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.5f).background(Black))
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(White))
        }
    } else {
        Row(modifier = modifier.clip(CircleShape).size(size).background(Transparent)) {
            Box(modifier = Modifier.width(size / 2).height(size).background(Black))
            Box(modifier = Modifier.width(size / 2).height(size).background(White))
        }
    }
}
