@file:Suppress("DestructuringDeclarationWithTooManyEntries")

package net.barrage.chatwhitelabel.ui.screens.profile.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.utils.StatusGreenBackground
import net.barrage.chatwhitelabel.utils.StatusGreenBorder
import net.barrage.chatwhitelabel.utils.StatusGreenIndicatorEnd
import net.barrage.chatwhitelabel.utils.StatusGreenIndicatorStart
import net.barrage.chatwhitelabel.utils.StatusRedBackground
import net.barrage.chatwhitelabel.utils.StatusRedBorder
import net.barrage.chatwhitelabel.utils.StatusRedIndicatorEnd
import net.barrage.chatwhitelabel.utils.StatusRedIndicatorStart

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
    val indicatorWidth by
        infiniteTransition.animateFloat(
            initialValue = 8f,
            targetValue = 10f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
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

    Row(
        modifier =
            modifier
                .border(1.dp, border, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .padding(vertical = 2.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(12.dp), contentAlignment = Alignment.Center) {
            Spacer(
                modifier =
                    Modifier.size(indicatorWidth.dp).clip(CircleShape).background(indicatorColor)
            )
        }
        Spacer(Modifier.width(8.dp))

        Text(
            text =
                if (active) {
                    "Active"
                } else {
                    "Inactive"
                },
            color = Color.Black,
            fontSize = 12.sp,
        )
    }
}
