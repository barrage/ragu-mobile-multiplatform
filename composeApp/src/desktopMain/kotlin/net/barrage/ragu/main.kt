package net.barrage.ragu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.barrage.ragu.di.KoinHelper
import net.barrage.ragu.utils.AppContext

fun main() = application {
    initApp()

    Window(onCloseRequest = ::exitApplication, title = "Ragu Desktop") { App() }
}

fun initApp() {
    KoinHelper.initKoin()
    Napier.base(DebugAntilog())
    AppContext.init()
}
