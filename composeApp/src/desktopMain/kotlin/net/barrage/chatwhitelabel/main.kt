package net.barrage.chatwhitelabel

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.barrage.chatwhitelabel.di.KoinHelper
import net.barrage.chatwhitelabel.utils.AppContext

fun main() = application {
    initApp()

    Window(onCloseRequest = ::exitApplication, title = "ChatWhitelabel") { App() }
}

fun initApp() {
    KoinHelper.initKoin()
    Napier.base(DebugAntilog())
    AppContext.init()
}
