package net.barrage.chatwhitelabel.utils.preferences

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

/**
 * The filename for storing settings preferences.
 */
internal const val SETTINGS_PREFERENCES_FILE = "settings_preferences.preferences_pb"

/**
 * Creates a DataStore for preferences.
 *
 * This is an expect function, which means its actual implementation
 * will be provided in platform-specific source sets (e.g., androidMain, iosMain).
 *
 * @param corruptionHandler Handler for dealing with data corruption.
 * @param coroutineScope The CoroutineScope to be used for the DataStore operations.
 * @param migrations List of migrations to be applied to the DataStore.
 * @return A DataStore instance for managing preferences.
 */
expect fun dataStorePreferences(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>,
): DataStore<Preferences>

/**
 * Creates a DataStore with default settings and a custom file path.
 *
 * @param corruptionHandler Handler for dealing with data corruption. Defaults to null.
 * @param coroutineScope The CoroutineScope to be used for the DataStore operations.
 *                       Defaults to a new scope with IO dispatcher and SupervisorJob.
 * @param migrations List of migrations to be applied to the DataStore. Defaults to an empty list.
 * @param path A function that returns the file path for the DataStore.
 * @return A DataStore instance for managing preferences.
 */
internal fun createDataStoreWithDefaults(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    migrations: List<DataMigration<Preferences>> = emptyList(),
    path: () -> String,
) =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = corruptionHandler,
        scope = coroutineScope,
        migrations = migrations,
        produceFile = { path().toPath() },
    )