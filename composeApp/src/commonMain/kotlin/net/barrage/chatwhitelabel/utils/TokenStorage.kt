package net.barrage.chatwhitelabel.utils

import net.barrage.chatwhitelabel.utils.preferences.AppPreferences

interface TokenStorage {
    suspend fun saveCookie(cookie: String)

    suspend fun getCookie(): String?

    suspend fun clearCookie()
}

class InMemoryTokenStorage : TokenStorage {
    private var cookie: String? = null

    override suspend fun saveCookie(cookie: String) {
        this.cookie = cookie
    }

    override suspend fun getCookie(): String? = cookie

    override suspend fun clearCookie() {
        cookie = null
    }
}

class DataStoreTokenStorage(private val appPreferences: AppPreferences) : TokenStorage {

    override suspend fun saveCookie(cookie: String) {
        appPreferences.saveCookie(cookie)
    }

    override suspend fun getCookie(): String? {
        return appPreferences.getCookie()
    }

    override suspend fun clearCookie() {
        appPreferences.clearCookie()
    }
}
