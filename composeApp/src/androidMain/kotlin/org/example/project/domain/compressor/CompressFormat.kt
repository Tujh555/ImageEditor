package org.example.project.domain.compressor

import android.graphics.Bitmap
import android.os.Build

internal sealed interface CompressFormat {

    val quantity: Int

    data class Jpeg(override val quantity: Int = HIGH_QUANTITY) : CompressFormat

    data class Png(override val quantity: Int = HIGH_QUANTITY) : CompressFormat

    data class Webp(override val quantity: Int = HIGH_QUANTITY) : CompressFormat

    companion object {
        const val LOW_QUANTITY = 20
        const val MEDIUM_QUANTITY = 50
        const val HIGH_QUANTITY = 80
        const val FULL_QUANTITY = 100
    }
}

internal fun CompressFormat.asBitmapCompressFormat() = when (this) {
    is CompressFormat.Jpeg ->
        Bitmap.CompressFormat.JPEG

    is CompressFormat.Png ->
        Bitmap.CompressFormat.PNG

    is CompressFormat.Webp ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
}

internal fun CompressFormat.getFileExtension() = when (this) {
    is CompressFormat.Jpeg -> ".jpg"
    is CompressFormat.Png -> ".png"
    is CompressFormat.Webp -> ".webp"
}