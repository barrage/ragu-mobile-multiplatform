package net.barrage.ragu.utils

import kotlin.experimental.ExperimentalNativeApi

/**
 * A platform-specific implementation of the `isDebug` property for iOS.
 *
 * This property determines whether the application is running in debug mode.
 * It uses the experimental `Platform.isDebugBinary` API to check if the current binary is a debug build.
 *
 * @OptIn(ExperimentalNativeApi::class) This property uses an experimental Native API.
 *
 * In debug builds, this will be true, allowing for debug-specific behavior.
 * In release builds, this will be false, ensuring that debug features are disabled.
 */
@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = Platform.isDebugBinary