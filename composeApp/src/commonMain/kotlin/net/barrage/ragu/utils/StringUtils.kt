package net.barrage.ragu.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeBase64ToByteArray(): ByteArray {

    val byteArray = encodeToByteArray()
    return Base64.decode(byteArray, 0, byteArray.size)
}