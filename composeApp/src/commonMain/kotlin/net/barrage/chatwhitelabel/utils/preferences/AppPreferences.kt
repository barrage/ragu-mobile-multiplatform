package net.barrage.chatwhitelabel.utils.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface AppPreferences {
    suspend fun isDarkModeEnabled(): Boolean

    suspend fun changeDarkMode(isEnabled: Boolean): Preferences

    suspend fun saveCookie(cookie: String): Preferences

    suspend fun getCookie(): String?

    suspend fun clearCookie(): Preferences
}

internal class AppPreferencesImpl(private val dataStore: DataStore<Preferences>) : AppPreferences {

    private companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val IS_DARK_MODE_ENABLED = "darkMode"
        private const val COOKIE = "cookie"
    }

    private val darkModeKey = booleanPreferencesKey("$PREFS_TAG_KEY$IS_DARK_MODE_ENABLED")
    private val cookieKey = stringPreferencesKey("$PREFS_TAG_KEY$COOKIE")

    override suspend fun isDarkModeEnabled() =
        dataStore.data.map { preferences -> preferences[darkModeKey] ?: false }.first()

    override suspend fun changeDarkMode(isEnabled: Boolean) =
        dataStore.edit { preferences -> preferences[darkModeKey] = isEnabled }

    override suspend fun saveCookie(cookie: String) =
        dataStore.edit { preferences -> preferences[cookieKey] = cookie }

    override suspend fun getCookie(): String? =
        dataStore.data.map { preferences -> preferences[cookieKey] }.first()

    override suspend fun clearCookie() =
        dataStore.edit { preferences -> preferences.remove(cookieKey) }
}
