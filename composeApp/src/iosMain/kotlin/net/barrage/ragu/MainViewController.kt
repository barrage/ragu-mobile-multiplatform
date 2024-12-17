package net.barrage.ragu

import androidx.compose.ui.window.ComposeUIViewController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.barrage.ragu.di.KoinHelper
import net.barrage.ragu.utils.AppContext

/**
 * Creates and returns the main UIViewController for the iOS app.
 *
 * This function uses Compose Multiplatform to create a UIViewController
 * that hosts the main App composable.
 *
 * @return A UIViewController that contains the main App composable.
 */
fun MainViewController() = ComposeUIViewController { App() }

/**
 * Initializes various components and libraries used in the app.
 *
 * This function should be called early in the app's lifecycle, typically
 * during app startup. It performs the following initializations:
 * 1. Initializes Firebase
 * 2. Enables Firebase Crashlytics
 * 3. Initializes Koin for dependency injection
 * 4. Sets up Napier for logging
 * 5. Initializes the AppContext
 */
fun initialise() {
    Firebase.initialize()
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    KoinHelper.initKoin()
    Napier.base(DebugAntilog())
    AppContext.init()
}