package net.barrage.ragu.utils.preferences

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 * iOS-specific implementation of the dataStorePreferences function.
 *
 * This function creates and returns a DataStore instance for managing preferences
 * using the DataStore library, adapted for iOS.
 *
 * @param corruptionHandler A handler for dealing with data corruption. Can be null.
 * @param coroutineScope The CoroutineScope to be used for DataStore operations.
 * @param migrations A list of data migrations to be applied to the DataStore.
 * @return A DataStore instance for managing preferences.
 *
 * @OptIn(ExperimentalForeignApi::class) This function uses experimental foreign API features.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun dataStorePreferences(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>,
): DataStore<Preferences> =
    createDataStoreWithDefaults(
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        coroutineScope = coroutineScope,
        path = {
            val documentDirectory: NSURL? =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            (requireNotNull(documentDirectory).path + "/$SETTINGS_PREFERENCES_FILE")
        },
    )