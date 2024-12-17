package net.barrage.ragu.utils

/**
 * Retrieves the Android version of the device.
 *
 * This is an expect function, which means its actual implementation
 * will be provided in platform-specific source sets (e.g., androidMain).
 *
 * @return An integer representing the Android SDK version.
 *         For non-Android platforms, this function is expected to return a default value (e.g., -1).
 */
expect fun getAndroidVersion(): Int