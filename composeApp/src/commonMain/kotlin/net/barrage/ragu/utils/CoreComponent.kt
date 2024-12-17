package net.barrage.ragu.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.plus
import net.barrage.ragu.utils.preferences.AppPreferences
import net.barrage.ragu.utils.preferences.AppPreferencesImpl
import net.barrage.ragu.utils.preferences.dataStorePreferences

/**
 * Defines the core component of the application, extending CoroutinesComponent.
 * This interface provides access to application-wide preferences.
 */
interface CoreComponent : CoroutinesComponent {
    /**
     * Provides access to application preferences.
     */
    val appPreferences: AppPreferences
}

/**
 * Internal implementation of the CoreComponent interface.
 * This class is responsible for initializing and managing core application components.
 */
internal class CoreComponentImpl internal constructor() :
    CoreComponent, CoroutinesComponent by CoroutinesComponentImpl.create() {

    /**
     * DataStore instance for managing application preferences.
     * Initialized with custom parameters including coroutine scope and empty migrations.
     */
    private val dataStore: DataStore<Preferences> =
        dataStorePreferences(
            corruptionHandler = null,
            coroutineScope = applicationScope + Dispatchers.IO,
            migrations = emptyList(),
        )

    /**
     * Implementation of AppPreferences, initialized with the DataStore instance.
     */
    override val appPreferences: AppPreferences = AppPreferencesImpl(dataStore)
}