package net.barrage.chatwhitelabel.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.plus
import net.barrage.chatwhitelabel.utils.preferences.AppPreferences
import net.barrage.chatwhitelabel.utils.preferences.AppPreferencesImpl
import net.barrage.chatwhitelabel.utils.preferences.dataStorePreferences

interface CoreComponent : CoroutinesComponent {
    val appPreferences: AppPreferences
}

internal class CoreComponentImpl internal constructor() :
    CoreComponent, CoroutinesComponent by CoroutinesComponentImpl.create() {

    private val dataStore: DataStore<Preferences> =
        dataStorePreferences(
            corruptionHandler = null,
            coroutineScope = applicationScope + Dispatchers.IO,
            migrations = emptyList(),
        )

    override val appPreferences: AppPreferences = AppPreferencesImpl(dataStore)
}
