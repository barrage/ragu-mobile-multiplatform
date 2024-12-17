package net.barrage.ragu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun fontFamily(): FontFamily {
    return FontFamily()
}

@Composable
fun customTypography(): CustomTypography {
    return CustomTypography(
        textBase =
        TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    )
}

data class CustomTypography(val textBase: TextStyle)
