package net.barrage.ragu.utils

import io.github.aakira.napier.Napier
import io.ktor.http.URLBuilder
import io.ktor.http.URLParserException

/**
 * Object responsible for parsing deep links and extracting relevant information.
 */
object DeepLinkParser {
    /**
     * Extracts the 'code' parameter from a deep link URL.
     *
     * @param urlString The deep link URL as a string.
     * @return The value of the 'code' parameter if the URL is valid, null otherwise.
     */
    fun extractCodeFromDeepLink(urlString: String): String? {
        val uri = parseUrl(urlString) ?: return null

        return if (isValidUri(uri)) {
            uri.parameters["code"]
        } else {
            null
        }
    }

    /**
     * Parses a URL string into a Ktor Url object.
     *
     * @param urlString The URL string to parse.
     * @return A Ktor Url object if parsing is successful, null otherwise.
     */
    private fun parseUrl(urlString: String): io.ktor.http.Url? {
        return try {
            URLBuilder(urlString).build()
        } catch (e: URLParserException) {
            Napier.e("Failed to parse URL: $urlString", e, tag = "DeepLinkParser")
            null
        }
    }

    /**
     * Checks if the given URI is valid for this application's deep linking.
     *
     * @param uri The Ktor Url object to validate.
     * @return true if the URI is valid, false otherwise.
     */
    private fun isValidUri(uri: io.ktor.http.Url): Boolean {
        return uri.host == "llmao-kotlin-api-development.barrage.dev" &&
                uri.encodedPath == "/oauthredirect"
    }
}