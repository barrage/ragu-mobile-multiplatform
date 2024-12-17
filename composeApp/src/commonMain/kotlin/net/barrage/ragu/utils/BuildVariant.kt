package net.barrage.ragu.utils

import io.github.aakira.napier.Napier

/**
 * Indicates whether the application is running in debug mode.
 * This is an expected property that should be implemented for each platform.
 */
expect val isDebug: Boolean

/**
 * Logs a debug message if the application is running in debug mode.
 *
 * @param message The message to be logged.
 * @param tag An optional tag for the log message. Defaults to "Ragu Multiplatform".
 */
fun debugLog(message: String, tag: String = "Ragu Multiplatform") {
    if (isDebug) {
        Napier.d(message, tag = tag)
    }
}

/**
 * Logs an error message with an optional throwable if the application is running in debug mode.
 *
 * @param message The error message to be logged.
 * @param throwable An optional Throwable associated with the error. Defaults to null.
 * @param tag An optional tag for the log message. Defaults to "Ragu Multiplatform".
 */
fun debugLogError(
    message: String,
    throwable: Throwable? = null,
    tag: String = "Ragu Multiplatform"
) {
    if (isDebug) {
        Napier.e(message, throwable, tag)
    }
}