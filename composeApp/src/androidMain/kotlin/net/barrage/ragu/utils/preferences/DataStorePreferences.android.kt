package net.barrage.ragu.utils.preferences

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import net.barrage.ragu.utils.applicationContext
import java.io.File

/**
 * Android-specific implementation of the dataStorePreferences function.
 *
 * This function creates and returns a DataStore instance for managing preferences
 * using Android's DataStore library.
 *
 * @param corruptionHandler A handler for dealing with data corruption. Can be null.
 * @param coroutineScope The CoroutineScope to be used for DataStore operations.
 * @param migrations A list of data migrations to be applied to the DataStore.
 * @return A DataStore instance for managing preferences.
 */
actual fun dataStorePreferences(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>,
): DataStore<Preferences> =
    createDataStoreWithDefaults(
        corruptionHandler = corruptionHandler,
        migrations = migrations,
        coroutineScope = coroutineScope,
        path = { File(applicationContext.filesDir, "datastore/$SETTINGS_PREFERENCES_FILE").path },
    )