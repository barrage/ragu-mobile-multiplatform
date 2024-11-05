@file:Suppress("ModifierComposed")

package net.barrage.chatwhitelabel.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import net.barrage.chatwhitelabel.ui.theme.GlobalsPrimaryMinus2

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition()
    val startOffsetX by
        transition.animateFloat(
            initialValue = -1.2f * size.width.toFloat(),
            targetValue = 1.2f * size.width.toFloat(),
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
        )

    background(
            brush =
                Brush.linearGradient(
                    colors =
                        listOf(
                            GlobalsPrimaryMinus2.copy(alpha = 0.8f),
                            GlobalsPrimaryMinus2.copy(alpha = 0.4f),
                            GlobalsPrimaryMinus2.copy(alpha = 0.8f),
                        ),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX + size.width.toFloat(), 0f),
                )
        )
        .onGloballyPositioned { size = it.size }
}
