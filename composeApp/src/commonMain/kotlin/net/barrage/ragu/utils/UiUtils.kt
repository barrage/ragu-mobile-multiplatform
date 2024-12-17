package net.barrage.ragu.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Retrieves the current screen width.
 *
 * This is an expect function, which means its actual implementation
 * will be provided in platform-specific source sets (e.g., androidMain, iosMain).
 *
 * @return The screen width as a [Dp] value.
 */
@Composable
expect fun getScreenWidth(): Dp

/**
 * Retrieves the current screen height.
 *
 * This is an expect function, which means its actual implementation
 * will be provided in platform-specific source sets (e.g., androidMain, iosMain).
 *
 * @return The screen height as a [Dp] value.
 */
@Composable
expect fun getScreenHeight(): Dp