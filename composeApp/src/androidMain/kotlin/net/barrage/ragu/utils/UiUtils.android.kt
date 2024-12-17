package net.barrage.ragu.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Retrieves the current screen width in density-independent pixels (dp).
 *
 * This is an Android-specific implementation of the `getScreenWidth` function.
 * It uses `LocalConfiguration` to get the screen width in dp.
 *
 * @return The screen width as a [Dp] value.
 */
@Composable
actual fun getScreenWidth(): Dp = LocalConfiguration.current.screenWidthDp.dp

/**
 * Retrieves the current screen height in density-independent pixels (dp).
 *
 * This is an Android-specific implementation of the `getScreenHeight` function.
 * It uses `LocalConfiguration` to get the screen height in dp.
 *
 * @return The screen height as a [Dp] value.
 */
@Composable
actual fun getScreenHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp