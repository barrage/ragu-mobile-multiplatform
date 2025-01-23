package net.barrage.ragu.utils

import androidx.compose.ui.unit.Dp
import platform.CoreGraphics.CGFloat

fun Dp.toCGFloat(): CGFloat = value.toDouble()