package net.barrage.chatwhitelabel.utils

import io.github.aakira.napier.Napier

expect val isDebug: Boolean

fun debugLog(message: String, tag: String = "ChatWhiteLabel") {
    if (isDebug) {
        Napier.d(message, tag = tag)
    }
}

fun debugLogError(message: String, throwable: Throwable? = null, tag: String = "ChatWhiteLabel") {
    if (isDebug) {
        Napier.e(message, throwable, tag)
    }
}
