package net.barrage.chatwhitelabel.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.barrage.chatwhitelabel.ui.theme.LocalCustomColorsPalette
import net.barrage.chatwhitelabel.ui.theme.customTypography

@Composable
fun NoInternetAlert(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "NO INTERNET CONNECTION",
                color = LocalCustomColorsPalette.current.textBase,
                style = customTypography().textBase,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "No internet connection detected. Please check your connection " +
                    "and try again to continue using the app.",
                color = LocalCustomColorsPalette.current.textBase,
                style = customTypography().textBase,
            )
        }
    }
}
