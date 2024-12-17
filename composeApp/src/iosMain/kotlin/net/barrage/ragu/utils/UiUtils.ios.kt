package net.barrage.ragu.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import platform.UIKit.UIScreen

/**
 * iOS-specific implementation to get the screen width.
 *
 * @return The screen width as a [Dp] value.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidth(): Dp = LocalWindowInfo.current.containerSize.width.pxToPoint().dp

/**
 * iOS-specific implementation to get the screen height.
 *
 * @return The screen height as a [Dp] value.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeight(): Dp = LocalWindowInfo.current.containerSize.height.pxToPoint().dp

/**
 * Extension function to convert pixels to points for iOS.
 *
 * This function takes into account the device's screen scale to convert
 * from pixels to points, which is necessary for accurate UI measurements on iOS.
 *
 * @return The value in points as a [Double].
 */
fun Int.pxToPoint(): Double = this.toDouble() / UIScreen.mainScreen.scale