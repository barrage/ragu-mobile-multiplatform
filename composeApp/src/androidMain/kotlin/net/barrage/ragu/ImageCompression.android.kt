package net.barrage.ragu

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual suspend fun ImageBitmap.encodeToByteArray(quality: Int): ByteArray? =
    withContext(Dispatchers.IO) {
        ByteArrayOutputStream().use { bytes ->
            this@encodeToByteArray.asAndroidBitmap()
                .compress(Bitmap.CompressFormat.JPEG, quality, bytes)
            bytes.toByteArray()
        }
    }