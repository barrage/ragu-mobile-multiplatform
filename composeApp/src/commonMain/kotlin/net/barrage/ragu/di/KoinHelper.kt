package net.barrage.ragu.di

import org.koin.core.context.startKoin

/**
 * Helper object for initializing Koin dependency injection.
 */
object KoinHelper {

    /**
     * Initializes Koin with all the modules defined in the application.
     *
     * This function starts Koin and loads all the modules defined in [allModules].
     * It should be called at the application's entry point to set up the dependency injection framework.
     *
     * @return The started Koin application context.
     */
    fun initKoin() = startKoin { modules(allModules()) }
}