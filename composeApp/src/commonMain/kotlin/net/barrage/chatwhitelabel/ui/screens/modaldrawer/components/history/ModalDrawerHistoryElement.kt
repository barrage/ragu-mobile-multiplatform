package net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.barrage.chatwhitelabel.ui.screens.modaldrawer.components.ModalDrawerHistoryViewState

@Composable
fun ModalDrawerHistoryElement(
    viewState: ModalDrawerHistoryViewState,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    Box(modifier.padding(4.dp).clickable { onClick(viewState.id) }) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            text = viewState.title,
        )
    }
}
