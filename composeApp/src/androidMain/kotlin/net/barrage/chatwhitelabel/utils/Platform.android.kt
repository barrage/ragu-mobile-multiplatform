package net.barrage.chatwhitelabel.utils

import android.os.Build

/**
 * Retrieves the Android SDK version of the device.
 *
 * This is an Android-specific implementation of the `getAndroidVersion` function.
 * It uses the `Build.VERSION.SDK_INT` constant to get the current Android SDK version.
 *
 * @return An integer representing the Android SDK version.
 */
actual fun getAndroidVersion(): Int {
    return Build.VERSION.SDK_INT
}