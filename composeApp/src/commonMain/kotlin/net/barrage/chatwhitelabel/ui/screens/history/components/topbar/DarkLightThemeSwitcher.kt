package net.barrage.chatwhitelabel.ui.screens.history.components.topbar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_moon
import chatwhitelabel.composeapp.generated.resources.ic_sun
import org.jetbrains.compose.resources.vectorResource

@Composable
fun DarkLightThemeSwitcher(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    movingDistance: Dp = 24.dp,
    iconSize: Dp = 24.dp,
    onClick: () -> Unit,
) {
    val rotation: Float by animateFloatAsState(if (isDarkTheme) 180f else 0f, label = "rotation")
    val distance: Dp by
    animateDpAsState(if (isDarkTheme) movingDistance else 0.dp, label = "distance")
    val color: Color by animateColorAsState(if (isDarkTheme) LightGray else Gray, label = "color")

    Box(
        modifier =
        modifier
            .width(movingDistance + iconSize + 8.dp)
            .clip(RoundedCornerShape(90.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .background(if (isDarkTheme) Black else White)
            .clickable { onClick() }
    ) {
        Crossfade(
            targetState = isDarkTheme,
            modifier =
            Modifier.wrapContentSize()
                .padding(4.dp)
                .size(iconSize)
                .offset(x = distance)
                .rotate(rotation),
        ) { isChecked ->
            Icon(
                imageVector =
                if (isChecked) vectorResource(Res.drawable.ic_moon)
                else vectorResource(Res.drawable.ic_sun),
                modifier =
                Modifier.size(iconSize)
                    .then(if (isChecked) Modifier.rotate(180F) else Modifier),
                contentDescription = "dark light theme switch",
                tint = color,
            )
        }
    }
}
