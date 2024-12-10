package net.barrage.chatwhitelabel.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kotlincrypto.SecureRandom
import org.kotlincrypto.hash.sha2.SHA256

/**
 * Utility object for PKCE (Proof Key for Code Exchange) operations.
 * This object provides methods for generating code verifiers and code challenges
 * as per the PKCE specification for OAuth 2.0.
 */
object PKCEUtil {
    /**
     * Generates a code verifier for PKCE.
     *
     * @return A Base64Url-encoded string to be used as the code verifier.
     */
    suspend fun generateCodeVerifier(): String =
        withContext(Dispatchers.Default) {
            val bytes = SecureRandom().nextBytesOf(32)
            bytes.encodeBase64Url()
        }

    /**
     * Generates a code challenge from a given code verifier.
     *
     * @param codeVerifier The code verifier to generate the challenge from.
     * @return A Base64Url-encoded string representing the code challenge.
     */
    suspend fun generateCodeChallenge(codeVerifier: String): String =
        withContext(Dispatchers.Default) {
            val bytes = codeVerifier.encodeToByteArray()
            val hash = SHA256().digest(bytes)
            hash.encodeBase64Url()
        }

    /**
     * Encodes a ByteArray to a Base64Url string.
     *
     * @return A Base64Url-encoded string.
     */
    private fun ByteArray.encodeBase64Url(): String {
        return this.encodeBase64().replace('+', '-').replace('/', '_').replace("=", "")
    }

    /**
     * Encodes a ByteArray to a standard Base64 string.
     *
     * @return A Base64-encoded string.
     */
    private fun ByteArray.encodeBase64(): String {
        val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val output = StringBuilder()
        var padding = 0
        var buffer = 0
        var bufferBits = 0

        for (byte in this) {
            buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
            bufferBits += 8
            while (bufferBits >= 6) {
                bufferBits -= 6
                output.append(table[(buffer shr bufferBits) and 0x3F])
            }
        }

        if (bufferBits > 0) {
            buffer = buffer shl (6 - bufferBits)
            output.append(table[buffer and 0x3F])
            padding = (3 - this.size % 3) % 3
        }

        repeat(padding) { output.append('=') }
        return output.toString()
    }
}