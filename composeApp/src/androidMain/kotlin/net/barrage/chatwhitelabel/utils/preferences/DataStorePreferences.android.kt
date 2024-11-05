package net.barrage.chatwhitelabel.utils.preferences

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import java.io.File
import kotlinx.coroutines.CoroutineScope
import net.barrage.chatwhitelabel.utils.applicationContext

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
