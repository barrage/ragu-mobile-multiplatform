package net.barrage.chatwhitelabel.utils

import io.github.aakira.napier.Napier
import io.ktor.http.URLBuilder
import io.ktor.http.URLParserException

object DeepLinkParser {
    fun extractCodeFromDeepLink(urlString: String): String? {
        val uri = parseUrl(urlString) ?: return null

        return if (isValidUri(uri)) {
            uri.parameters["code"]
        } else {
            null
        }
    }

    private fun parseUrl(urlString: String): io.ktor.http.Url? {
        return try {
            URLBuilder(urlString).build()
        } catch (e: URLParserException) {
            Napier.e("Failed to parse URL: $urlString", e, tag = "DeepLinkParser")
            null
        }
    }

    private fun isValidUri(uri: io.ktor.http.Url): Boolean {
        return uri.host == "llmao-kotlin-api-staging.m2.barrage.beer" &&
            uri.encodedPath == "/oauthredirect"
    }
}
