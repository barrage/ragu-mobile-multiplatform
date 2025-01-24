package net.barrage.ragu

import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun ImageBitmap.encodeToByteArray(quality: Int): ByteArray?
