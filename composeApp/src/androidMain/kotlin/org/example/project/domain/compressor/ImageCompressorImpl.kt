package tech.inno.dion.chat.image.editor.compressor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import org.example.project.domain.CompressFormat
import org.example.project.domain.Resolution
import org.example.project.domain.compressor.ImageCompressor
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.log2

@Suppress("DEPRECATION")
internal class ImageCompressorImpl(private val context: Context) : ImageCompressor {

    override fun compress(
        original: Uri,
        saveToFolder: String,
        fileName: String,
        targetResolution: Resolution,
        format: CompressFormat?,
    ): Uri? {
        val realImageResolution = original.getResolution()

        val bitmap = if (realImageResolution.maxSide <= targetResolution.maxSide) {
            context.loadBitmap(uri = original)
        } else {
            loadScaledBitmap(
                bitmapUri = original,
                realResolution = realImageResolution,
                targetResolution = targetResolution
            )
        }

        return if (bitmap == null || format == null) {
            original
        } else {
            bitmap.use {
                writeCompressed(
                    original = it,
                    compressFormat = format,
                    folderPath = saveToFolder,
                    fileName = fileName,
                )
            }
        }
    }

    private fun loadScaledBitmap(
        bitmapUri: Uri,
        realResolution: Resolution,
        targetResolution: Resolution,
    ): Bitmap? {
        val scale = calculateInSampleSize(
            real = realResolution.maxSide,
            target = targetResolution.maxSide
        )

        val options = BitmapFactory.Options().apply {
            inSampleSize = scale
            inJustDecodeBounds = false
        }
        return BitmapFactory.decodeFile(bitmapUri.path, options)
    }

    private fun writeCompressed(
        original: Bitmap,
        folderPath: String,
        fileName: String,
        compressFormat: CompressFormat,
    ): Uri? {
        val extension = compressFormat.getFileExtension()
        val nameWithoutExtension = fileName.removeExtension()
        val compressedFile = File(folderPath, "${nameWithoutExtension}_cmp$extension")
            .apply {
                if (exists()) {
                    delete()
                }

                createNewFile()
            }

        val isCompressed = compressedFile.outputStream()
            .use { outputStream ->
                original.compress(
                    compressFormat.asBitmapCompressFormat(),
                    compressFormat.quantity,
                    outputStream
                )
            }

        return if (isCompressed) {
            compressedFile.toUri()
        } else {
            compressedFile.delete()
            null
        }
    }

    private fun Uri.getResolution(): Resolution {
        val path = this.path ?: return Resolution.ZERO
        val bitmapOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(path, bitmapOptions)
        return Resolution(
            width = bitmapOptions.outWidth,
            height = bitmapOptions.outHeight
        )
    }

    private fun Context.loadBitmap(uri: Uri) =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder
                    .createSource(contentResolver, uri)
                    .let(ImageDecoder::decodeBitmap)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: FileNotFoundException) {
            null
        } catch (e: IOException) {
            null
        }

    private fun CompressFormat.asBitmapCompressFormat() = when (this) {
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

    private fun CompressFormat.getFileExtension() = when (this) {
        is CompressFormat.Jpeg -> ".jpg"
        is CompressFormat.Png -> ".png"
        is CompressFormat.Webp -> ".webp"
    }

    private inline fun <T> Bitmap.use(action: (Bitmap) -> T): T {
        val result = action(this)
        recycle()
        return result
    }

    private fun String.removeExtension(): String {
        val dotIndex = indexOfLast { it == '.' }

        return if (dotIndex == -1) {
            this
        } else {
            substring(0, dotIndex)
        }
    }

    private fun calculateInSampleSize(real: Int, target: Int): Int {
        val ratio = real.toFloat() / target

        if (ratio <= 2f) return 2

        val degree = ceil(log2(ratio)).toInt()

        return 1 shl degree
    }
}