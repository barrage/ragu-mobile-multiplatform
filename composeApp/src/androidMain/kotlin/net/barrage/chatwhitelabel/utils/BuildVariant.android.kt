package net.barrage.chatwhitelabel.utils

import net.barrage.chatwhitelabel.BuildConfig

/**
 * A platform-specific implementation of the `isDebug` property for Android.
 *
 * This property determines whether the application is running in debug mode.
 * It uses the `BuildConfig.DEBUG` flag, which is automatically set by the Android build system
 * based on the current build variant (debug or release).
 *
 * In debug builds, this will be true, allowing for debug-specific behavior.
 * In release builds, this will be false, ensuring that debug features are disabled.
 */
actual val isDebug: Boolean = BuildConfig.DEBUG