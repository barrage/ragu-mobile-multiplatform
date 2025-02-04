package net.barrage.ragu.ui.screens.profile.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.ragu.ui.theme.StatusGreenBackground
import net.barrage.ragu.ui.theme.StatusGreenBorder
import net.barrage.ragu.ui.theme.StatusGreenIndicatorEnd
import net.barrage.ragu.ui.theme.StatusGreenIndicatorStart
import net.barrage.ragu.ui.theme.StatusRedBackground
import net.barrage.ragu.ui.theme.StatusRedBorder
import net.barrage.ragu.ui.theme.StatusRedIndicatorEnd
import net.barrage.ragu.ui.theme.StatusRedIndicatorStart
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import org.jetbrains.compose.resources.stringResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.active
import ragumultiplatform.composeapp.generated.resources.inactive

@Composable
fun StatusIndicator(active: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val (background, border, start, end) =
        if (active) {
            listOf(
                StatusGreenBackground,
                StatusGreenBorder,
                StatusGreenIndicatorStart,
                StatusGreenIndicatorEnd,
            )
        } else {
            listOf(
                StatusRedBackground,
                StatusRedBorder,
                StatusRedIndicatorStart,
                StatusRedIndicatorEnd,
            )
        }
    val scale by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec =
        infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
    )

    val indicatorColor by
    infiniteTransition.animateColor(
        initialValue = start,
        targetValue = end,
        animationSpec =
        infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, border),
        modifier = modifier,
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier =
                Modifier.scale(scale).size(10.dp).clip(CircleShape).background(indicatorColor)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text =
                if (active) {
                    stringResource(Res.string.active)
                } else {
                    stringResource(Res.string.inactive)
                },
                color = Color(0xFF222222),
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall.fixCenterTextOnAllPlatforms(),
            )
        }
    }
}
