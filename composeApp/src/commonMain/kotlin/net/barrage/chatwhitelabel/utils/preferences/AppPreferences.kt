package net.barrage.chatwhitelabel.utils.preferences

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface ThemePreferences {
    suspend fun isDarkModeEnabled(): Boolean

    suspend fun getThemeColor(): Color

    suspend fun getThemeVariant(): PaletteStyle

    suspend fun saveThemeColor(color: Color): Preferences

    suspend fun saveThemeVariant(variant: PaletteStyle): Preferences

    suspend fun changeDarkMode(isEnabled: Boolean): Preferences
}

interface AuthPreferences {
    suspend fun saveCookie(cookie: String): Preferences

    suspend fun getCookie(): String?

    suspend fun clearCookie(): Preferences

    suspend fun saveCodeVerifier(codeVerifier: String): Preferences

    suspend fun getCodeVerifier(): String?

    suspend fun clearCodeVerifier(): Preferences
}

interface AppPreferences : ThemePreferences, AuthPreferences {
    suspend fun clear(): Preferences
}

internal class AppPreferencesImpl(private val dataStore: DataStore<Preferences>) :
    AppPreferences,
    ThemePreferences by ThemePreferencesImpl(dataStore),
    AuthPreferences by AuthPreferencesImpl(dataStore) {

    override suspend fun clear(): Preferences =
        dataStore.edit { preferences -> preferences.clear() }
}

private class ThemePreferencesImpl(private val dataStore: DataStore<Preferences>) :
    ThemePreferences {
    private companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val IS_DARK_MODE_ENABLED = "darkMode"
        private const val THEME = "theme"
        private const val VARIANT = "variant"
    }

    private val darkModeKey = booleanPreferencesKey("$PREFS_TAG_KEY$IS_DARK_MODE_ENABLED")
    private val themeKey = intPreferencesKey("$PREFS_TAG_KEY$THEME")
    private val variantKey = stringPreferencesKey("$PREFS_TAG_KEY$VARIANT")

    override suspend fun isDarkModeEnabled() =
        dataStore.data.map { preferences -> preferences[darkModeKey] ?: false }.first()

    override suspend fun getThemeColor(): Color {
        val color =
            (dataStore.data.map { preferences -> preferences[themeKey] }.first() ?: White.toArgb())
        return Color(color)
    }

    override suspend fun getThemeVariant(): PaletteStyle {
        val variant =
            (dataStore.data.map { preferences -> preferences[variantKey] }.first())
                ?: PaletteStyle.TonalSpot.name
        return PaletteStyle.valueOf(variant)
    }

    override suspend fun saveThemeColor(color: Color): Preferences {
        return dataStore.edit { preferences -> preferences[themeKey] = color.toArgb() }
    }

    override suspend fun saveThemeVariant(variant: PaletteStyle): Preferences {
        return dataStore.edit { preferences -> preferences[variantKey] = variant.name }
    }

    override suspend fun changeDarkMode(isEnabled: Boolean) =
        dataStore.edit { preferences -> preferences[darkModeKey] = isEnabled }
}

private class AuthPreferencesImpl(private val dataStore: DataStore<Preferences>) : AuthPreferences {
    private companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val COOKIE = "cookie"
        private const val CODE_VERIFIER = "codeVerifier"
    }

    private val cookieKey = stringPreferencesKey("$PREFS_TAG_KEY$COOKIE")
    private val codeVerifierKey = stringPreferencesKey("$PREFS_TAG_KEY$CODE_VERIFIER")

    override suspend fun saveCookie(cookie: String) =
        dataStore.edit { preferences -> preferences[cookieKey] = cookie }

    override suspend fun getCookie(): String? =
        dataStore.data.map { preferences -> preferences[cookieKey] }.first()

    override suspend fun clearCookie() =
        dataStore.edit { preferences -> preferences.remove(cookieKey) }

    override suspend fun saveCodeVerifier(codeVerifier: String) =
        dataStore.edit { preferences -> preferences[codeVerifierKey] = codeVerifier }

    override suspend fun getCodeVerifier(): String? =
        dataStore.data.map { preferences -> preferences[codeVerifierKey] }.first()

    override suspend fun clearCodeVerifier() =
        dataStore.edit { preferences -> preferences.remove(codeVerifierKey) }
}
