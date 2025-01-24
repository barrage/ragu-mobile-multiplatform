package net.barrage.ragu.utils

fun detectImageMimeType(bytes: ByteArray): String {
    return when {
        bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() -> "image/jpeg"
        bytes.size >= 8 &&
                bytes[0] == 0x89.toByte() &&
                bytes[1] == 0x50.toByte() && // P
                bytes[2] == 0x4E.toByte() && // N
                bytes[3] == 0x47.toByte() && // G
                bytes[4] == 0x0D.toByte() && // CR
                bytes[5] == 0x0A.toByte() && // LF
                bytes[6] == 0x1A.toByte() && // EOF
                bytes[7] == 0x0A.toByte()    // LF
            -> "image/png"

        bytes.size >= 3 &&
                bytes[0] == 0x47.toByte() && // G
                bytes[1] == 0x49.toByte() && // I
                bytes[2] == 0x46.toByte()    // F
            -> "image/gif"

        bytes.size >= 2 &&
                bytes[0] == 0x42.toByte() && // B
                bytes[1] == 0x4D.toByte()    // M
            -> "image/bmp"

        bytes.size >= 4 &&
                bytes[0] == 0x52.toByte() && // R
                bytes[1] == 0x49.toByte() && // I
                bytes[2] == 0x46.toByte() && // F
                bytes[3] == 0x46.toByte()    // F
            -> "image/webp"

        else -> "application/octet-stream" // fallback
    }
}