package net.barrage.chatwhitelabel.ui.screens.history.components.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.domain.model.HistoryElement
import net.barrage.chatwhitelabel.utils.fixCenterTextOnAllPlatforms

@Composable
fun ModalDrawerHistoryElement(
    viewState: HistoryElement,
    modifier: Modifier = Modifier,
    onClick: (HistoryElement) -> Unit,
) {
    Box(
        modifier =
            modifier
                .then(
                    if (viewState.isSelected) {
                        Modifier.background(MaterialTheme.colorScheme.surfaceDim)
                    } else {
                        Modifier
                    }
                )
                .clickable { onClick(viewState) }
                .padding(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().padding(start = 12.dp),
            style = MaterialTheme.typography.titleMedium.fixCenterTextOnAllPlatforms(),
            text = viewState.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
