package net.barrage.ragu.utils.preferences

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

/**
 * Interface for managing theme-related preferences.
 */
interface ThemePreferences {
    /**
     * Saves the dark mode setting.
     * @param isEnabled Whether dark mode should be enabled or disabled.
     * @return The updated Preferences.
     */
    suspend fun saveDarkModeEnabled(isEnabled: Boolean): Preferences

    /**
     * Checks if dark mode is enabled.
     * @return true if dark mode is enabled, false otherwise.
     */
    suspend fun getDarkModeEnabled(): Boolean

    /**
     * Clears the dark mode setting.
     * @return The updated Preferences.
     */
    suspend fun clearDarkModeEnabled(): Preferences

    /**
     * Saves a new theme color.
     * @param color The Color to be saved as the new theme color.
     * @return The updated Preferences.
     */
    suspend fun saveThemeColor(color: Color): Preferences

    /**
     * Retrieves the current theme color.
     * @return The current Color used for theming.
     */
    suspend fun getThemeColor(): Color

    /**
     * Clears the saved theme color.
     * @return The updated Preferences.
     */
    suspend fun clearThemeColor(): Preferences

    /**
     * Saves a new theme variant.
     * @param variant The PaletteStyle to be saved as the new theme variant.
     * @return The updated Preferences.
     */
    suspend fun saveThemeVariant(variant: PaletteStyle): Preferences

    /**
     * Retrieves the current theme variant.
     * @return The current PaletteStyle used for theming.
     */
    suspend fun getThemeVariant(): PaletteStyle

    /**
     * Clears the saved theme variant.
     * @return The updated Preferences.
     */
    suspend fun clearThemeVariant(): Preferences
}

/**
 * Interface for managing authentication-related preferences.
 */
interface AuthPreferences {
    /**
     * Saves an authentication cookie.
     * @param cookie The cookie string to be saved.
     * @return The updated Preferences.
     */
    suspend fun saveCookie(cookie: String): Preferences

    /**
     * Retrieves the saved authentication cookie.
     * @return The saved cookie string, or null if not present.
     */
    suspend fun getCookie(): String?

    /**
     * Clears the saved authentication cookie.
     * @return The updated Preferences.
     */
    suspend fun clearCookie(): Preferences

    /**
     * Saves a PKCE code verifier.
     * @param codeVerifier The code verifier string to be saved.
     * @return The updated Preferences.
     */
    suspend fun saveCodeVerifier(codeVerifier: String): Preferences

    /**
     * Retrieves the saved PKCE code verifier.
     * @return The saved code verifier string, or null if not present.
     */
    suspend fun getCodeVerifier(): String?

    /**
     * Clears the saved PKCE code verifier.
     * @return The updated Preferences.
     */
    suspend fun clearCodeVerifier(): Preferences
}

/**
 * Interface for the reveal app tutorial
 */
interface TutorialPreferences {
    suspend fun saveShouldShowOnboardingTutorial(shouldShowTutorial: Boolean): Preferences
    suspend fun getShouldShowOnboardingTutorial(): Boolean
    suspend fun saveShouldShowChatTitleTutorial(shouldShowTutorial: Boolean): Preferences
    suspend fun getShouldShowChatTitleTutorial(): Boolean
}

/**
 * Combined interface for all app preferences, including theme and authentication preferences.
 */
interface AppPreferences : ThemePreferences, AuthPreferences, TutorialPreferences {
    /**
     * Clears all saved preferences.
     * @return The updated (empty) Preferences.
     */
    suspend fun clear()
}

/**
 * Implementation of AppPreferences that delegates to separate implementations for theme and auth preferences.
 * @param dataStore The DataStore used for storing preferences.
 */
internal class AppPreferencesImpl(private val dataStore: DataStore<Preferences>) :
    AppPreferences,
    ThemePreferences by ThemePreferencesImpl(dataStore),
    AuthPreferences by AuthPreferencesImpl(dataStore),
    TutorialPreferences by TutorialPreferencesImpl(dataStore) {

    override suspend fun clear() {
        clearDarkModeEnabled()
        clearThemeColor()
        clearThemeVariant()
        clearCookie()
        clearCodeVerifier()
    }
}

/**
 * Implementation of ThemePreferences using DataStore.
 * @param dataStore The DataStore used for storing theme preferences.
 */
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
    override suspend fun saveDarkModeEnabled(isEnabled: Boolean): Preferences =
        dataStore.edit { preferences -> preferences[darkModeKey] = isEnabled }

    override suspend fun getDarkModeEnabled(): Boolean =
        dataStore.data.map { preferences -> preferences[darkModeKey] ?: false }.first()

    override suspend fun clearDarkModeEnabled(): Preferences =
        dataStore.edit { preferences -> preferences.remove(darkModeKey) }

    override suspend fun saveThemeColor(color: Color): Preferences =
        dataStore.edit { preferences -> preferences[themeKey] = color.toArgb() }

    override suspend fun getThemeColor(): Color {
        val color =
            (dataStore.data.map { preferences -> preferences[themeKey] }.first() ?: White.toArgb())
        return Color(color)
    }

    override suspend fun clearThemeColor(): Preferences =
        dataStore.edit { preferences -> preferences.remove(themeKey) }

    override suspend fun saveThemeVariant(variant: PaletteStyle): Preferences =
        dataStore.edit { preferences -> preferences[variantKey] = variant.name }

    override suspend fun getThemeVariant(): PaletteStyle {
        val variant =
            (dataStore.data.map { preferences -> preferences[variantKey] }.first())
                ?: PaletteStyle.TonalSpot.name
        return PaletteStyle.valueOf(variant)
    }

    override suspend fun clearThemeVariant(): Preferences =
        dataStore.edit { preferences -> preferences.remove(variantKey) }
}

/**
 * Implementation of AuthPreferences using DataStore.
 * @param dataStore The DataStore used for storing authentication preferences.
 */
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

/**
 * Implementation of TutorialPreferences using DataStore.
 * @param dataStore The DataStore used for storing tutorial preferences.
 */

private class TutorialPreferencesImpl(private val dataStore: DataStore<Preferences>) :
    TutorialPreferences {
    private companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val ONBOARDING_TUTORIAL_REVEALED = "tutorialRevealed"
        private const val CHAT_TITLE_TUTORIAL_REVEALED = "chatTitleTutorialRevealed"
    }

    private val showOnboardingTutorialKey =
        booleanPreferencesKey("$PREFS_TAG_KEY$ONBOARDING_TUTORIAL_REVEALED")
    private val showChatTitleTutorialKey =
        booleanPreferencesKey("$PREFS_TAG_KEY$CHAT_TITLE_TUTORIAL_REVEALED")

    override suspend fun saveShouldShowOnboardingTutorial(shouldShowTutorial: Boolean): Preferences =
        dataStore.edit { preferences ->
            preferences[showOnboardingTutorialKey] = shouldShowTutorial
        }

    override suspend fun getShouldShowOnboardingTutorial(): Boolean =
        dataStore.data.map { preferences -> preferences[showOnboardingTutorialKey] ?: true }.first()

    override suspend fun getShouldShowChatTitleTutorial(): Boolean =
        dataStore.data.map { preferences -> preferences[showChatTitleTutorialKey] ?: true }.first()


    override suspend fun saveShouldShowChatTitleTutorial(shouldShowTutorial: Boolean): Preferences =
        dataStore.edit { preferences -> preferences[showChatTitleTutorialKey] = shouldShowTutorial }
}