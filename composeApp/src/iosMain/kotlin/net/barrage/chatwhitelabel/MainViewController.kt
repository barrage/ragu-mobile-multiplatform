@file:Suppress("FunctionNaming")

package net.barrage.chatwhitelabel

import App
import androidx.compose.ui.window.ComposeUIViewController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.barrage.chatwhitelabel.di.KoinHelper
import net.barrage.chatwhitelabel.utils.AppContext

fun MainViewController() = ComposeUIViewController { App() }

fun initialise() {
    Firebase.initialize()
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    KoinHelper.initKoin()
    Napier.base(DebugAntilog())
    AppContext.init()
}
