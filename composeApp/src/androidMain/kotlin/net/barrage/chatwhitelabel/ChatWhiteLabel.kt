package net.barrage.chatwhitelabel

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.barrage.chatwhitelabel.di.KoinHelper
import net.barrage.chatwhitelabel.utils.AppContext

class ChatWhiteLabel : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinHelper.initKoin()
        Napier.base(DebugAntilog())
        AppContext.init()
    }
}
