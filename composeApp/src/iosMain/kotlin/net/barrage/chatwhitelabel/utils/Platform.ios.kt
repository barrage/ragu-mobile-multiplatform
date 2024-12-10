package net.barrage.chatwhitelabel.utils

/**
 * iOS-specific implementation of the getAndroidVersion function.
 *
 * This function is part of a Kotlin Multiplatform project where different
 * platforms may need to provide their own implementations of common functions.
 *
 * On iOS, there is no concept of an Android version, so this function
 * returns a sentinel value (-1) to indicate that it's not applicable.
 *
 * @return Always returns -1 on iOS to indicate that Android version is not applicable.
 */
actual fun getAndroidVersion(): Int {
    return -1
}