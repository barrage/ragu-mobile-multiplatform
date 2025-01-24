package net.barrage.ragu

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

actual suspend fun ImageBitmap.encodeToByteArray(quality: Int): ByteArray? =
    withContext(Dispatchers.IO) {
        Image.makeFromBitmap(this@encodeToByteArray.asSkiaBitmap())
            .encodeToData(org.jetbrains.skia.EncodedImageFormat.JPEG, quality)?.bytes
    }