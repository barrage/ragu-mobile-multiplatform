@file:Suppress("MatchingDeclarationName")

package net.barrage.chatwhitelabel.utils

import android.os.Build

actual fun getAndroidVersion(): Int {
    return Build.VERSION.SDK_INT
}
