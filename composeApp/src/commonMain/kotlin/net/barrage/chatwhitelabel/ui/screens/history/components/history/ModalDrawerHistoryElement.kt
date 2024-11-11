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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.domain.model.HistoryElement

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
                        Modifier.background(MaterialTheme.colorScheme.surfaceTint)
                    } else {
                        Modifier
                    }
                )
                .clickable { onClick(viewState.id) }
                .padding(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().padding(start = 12.dp),
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            text = viewState.title,
        )
    }
}
