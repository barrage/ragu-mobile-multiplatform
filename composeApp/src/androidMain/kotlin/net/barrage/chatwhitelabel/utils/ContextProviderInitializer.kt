package net.barrage.chatwhitelabel.utils

import android.content.Context
import androidx.startup.Initializer

/**
 * A global variable to hold the application context.
 * It's initialized lazily and can only be set internally.
 */
internal lateinit var applicationContext: Context
    private set

/**
 * A marker object used as the return type for the ContextProvider initializer.
 */
data object ContextProviderInitializer

/**
 * An initializer class that provides the application context.
 * This class is used with the App Startup library to initialize the application context early in the app's lifecycle.
 */
class ContextProvider : Initializer<ContextProviderInitializer> {
    /**
     * Creates and initializes the ContextProviderInitializer.
     * This method is called by the App Startup library during app initialization.
     *
     * @param context The context provided by the system.
     * @return The ContextProviderInitializer object.
     */
    override fun create(context: Context): ContextProviderInitializer {
        applicationContext = context.applicationContext
        return ContextProviderInitializer
    }

    /**
     * Specifies the list of dependencies for this initializer.
     * In this case, there are no dependencies.
     *
     * @return An empty list as this initializer has no dependencies.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}