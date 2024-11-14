package net.barrage.chatwhitelabel.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import com.materialkolor.PaletteStyle
import kotlinx.collections.immutable.persistentListOf

val BluePrimary = Color(0xFF3242bc)
val VioletPrimary = Color(0xFF794ae9)
val RedPrimary = Color(0xFFce1c1c)
val GreenPrimary = Color(0xFF00b367)
val OrangePrimary = Color(0xFFf97012)
val MagentaPrimary = Color(0xFFe75fb3)
val YellowPrimary = Color(0xFFb38600)
val LimePrimary = Color(0xFF6a9a23)
val TealPrimary = Color(0xFF2898bd)
val BrownPrimary = Color(0xFFa5712e)
val SagePrimary = Color(0xFF6b7d6b)

val StatusGreenBackground = Color(0xFFd7f8e5)
val StatusGreenBorder = Color(0xFF63cf85)
val StatusGreenIndicatorStart = Color(0xFF5ac077)
val StatusGreenIndicatorEnd = Color(0xFFbdebcc)

val StatusRedBackground = Color(0xFFF8D7DA)
val StatusRedBorder = Color(0xFFCF636B)
val StatusRedIndicatorStart = Color(0xFFC05763)
val StatusRedIndicatorEnd = Color(0xFFF2C2C5)

val ThemeColors =
    persistentListOf(
        White,
        SagePrimary,
        TealPrimary,
        BluePrimary,
        VioletPrimary,
        LimePrimary,
        GreenPrimary,
        YellowPrimary,
        OrangePrimary,
        RedPrimary,
        MagentaPrimary,
        BrownPrimary,
    )

val PaletteVariants =
    persistentListOf(
        PaletteStyle.TonalSpot,
        PaletteStyle.Neutral,
        PaletteStyle.Vibrant,
        PaletteStyle.Expressive,
        PaletteStyle.Rainbow,
        PaletteStyle.FruitSalad,
        PaletteStyle.Monochrome,
        PaletteStyle.Fidelity,
        PaletteStyle.Content,
    )
