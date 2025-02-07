package net.barrage.ragu.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.ic_ragu

@Composable
fun AppIconCard(modifier: Modifier = Modifier) {
    CustomCard(
        cardVariant = CardVariant.Primary(isOutlined = true),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_ragu),
            contentDescription = null,
            modifier = Modifier.size(90.dp).padding(12.dp),
        )
    }
}
