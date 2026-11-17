package de.surroundlab.app.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Generates QR code ImageBitmaps locally using ZXing core.
 */
object QrCodeGenerator {

    fun generateQrBitmap(content: String, sizePx: Int = 512): ImageBitmap? {
        if (content.isBlank()) return null
        return runCatching {
            val hints = mapOf(
                EncodeHintType.MARGIN to 1
            )
            val matrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
                hints
            )
            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (matrix.get(x, y)) {
                        Color.rgb(0, 55, 55) // Dark Teal (#003737) on pure white for crisp contrast
                    } else {
                        Color.WHITE
                    }
                }
            }
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bmp.setPixels(pixels, 0, width, 0, 0, width, height)
            bmp.asImageBitmap()
        }.getOrNull()
    }
}
