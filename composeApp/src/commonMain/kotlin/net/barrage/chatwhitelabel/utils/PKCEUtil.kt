package net.barrage.chatwhitelabel.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kotlincrypto.SecureRandom
import org.kotlincrypto.hash.sha2.SHA256

object PKCEUtil {
    suspend fun generateCodeVerifier(): String =
        withContext(Dispatchers.Default) {
            val bytes = SecureRandom().nextBytesOf(32)
            bytes.encodeBase64Url()
        }

    suspend fun generateCodeChallenge(codeVerifier: String): String =
        withContext(Dispatchers.Default) {
            val bytes = codeVerifier.encodeToByteArray()
            val hash = SHA256().digest(bytes)
            hash.encodeBase64Url()
        }

    private fun ByteArray.encodeBase64Url(): String {
        return this.encodeBase64().replace('+', '-').replace('/', '_').replace("=", "")
    }

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
