package net.barrage.chatwhitelabel.ui.screens.history.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun VariantCard(
    isSelected: Boolean,
    variant: PaletteStyle,
    modifier: Modifier = Modifier,
    onClick: (PaletteStyle) -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { onClick(variant) }
                .then(
                    if (isSelected) {
                        Modifier.background(MaterialTheme.colorScheme.onTertiary)
                    } else {
                        Modifier.border(
                            1.dp,
                            MaterialTheme.colorScheme.onSecondaryContainer,
                            RoundedCornerShape(6.dp),
                        )
                    }
                )
    ) {
        Text(
            text = variant.name,
            style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
            modifier = Modifier.padding(4.dp),
        )
    }
}
