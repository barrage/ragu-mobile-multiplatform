package net.barrage.chatwhitelabel

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.barrage.chatwhitelabel.di.KoinHelper
import net.barrage.chatwhitelabel.utils.AppContext

/**
 * Custom Application class for the ChatWhiteLabel app.
 * This class is responsible for initializing app-wide components and configurations.
 */
class ChatWhiteLabel : Application() {

    /**
     * Called when the application is starting, before any other application objects have been created.
     * Overridden to initialize key components of the app.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin for dependency injection
        KoinHelper.initKoin()

        // Set up Napier for logging with DebugAntilog
        Napier.base(DebugAntilog())

        // Initialize the AppContext
        AppContext.init()
    }
}