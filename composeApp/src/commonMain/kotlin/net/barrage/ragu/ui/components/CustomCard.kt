package net.barrage.ragu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

sealed class CardVariant(val outlined: Boolean = false, val selected: Boolean = false) {
    data class Primary(val isOutlined: Boolean = false, val isSelected: Boolean = true) :
        CardVariant(isOutlined, isSelected)

    data class Secondary(val isOutlined: Boolean = false, val isSelected: Boolean = true) :
        CardVariant(isOutlined, isSelected)

    data class Error(val isOutlined: Boolean = false, val isSelected: Boolean = true) :
        CardVariant(isOutlined, isSelected)
}

@Composable
fun CustomCard(
    cardVariant: CardVariant,
    shape: Shape = CardDefaults.shape,
    elevation: CardElevation = CardDefaults.cardElevation(),
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val cardColors = when (cardVariant) {
        is CardVariant.Primary -> CardDefaults.cardColors(
            containerColor = if (cardVariant.selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                alpha = 0.6f
            ),
            contentColor = if (cardVariant.selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(
                alpha = 0.6f
            )
        )

        is CardVariant.Secondary -> CardDefaults.cardColors(
            containerColor = if (cardVariant.selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.6f
            ),
            contentColor = if (cardVariant.selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.6f
            )
        )

        is CardVariant.Error -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
    Card(
        colors = cardColors,
        border = if (cardVariant.outlined) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primaryContainer
        ) else null,
        onClick = onClick,
        shape = shape,
        elevation = elevation,
        modifier = modifier,
    ) {
        content()
    }
}