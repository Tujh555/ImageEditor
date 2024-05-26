package org.example.project.domain.uc

import android.content.Context
import android.graphics.Bitmap
import implementation.domain.models.Image
import implementation.domain.repository.ImageRepository
import kotlinx.datetime.Clock
import org.example.project.child
import org.example.project.domain.compressor.CompressFormat
import org.example.project.domain.compressor.asBitmapCompressFormat
import org.example.project.domain.compressor.getFileExtension
import org.example.project.imageRootDirectory
import java.util.UUID

internal class SaveBitmap(
    private val context: Context,
    private val repository: ImageRepository
) {
    operator fun invoke(
        bitmap: Bitmap,
        format: CompressFormat
    ) {
        val outputFile = context.imageRootDirectory
            .child("${Clock.System.now().toEpochMilliseconds()}${format.getFileExtension()}")

        outputFile
            .outputStream()
            .use { output ->
                bitmap.compress(format.asBitmapCompressFormat(), 100, output)
            }

        val image = Image(
            id = UUID.randomUUID().toString(),
            name = outputFile.name,
            saveDate = Clock.System.now(),
            path = outputFile.absolutePath
        )

        repository.insert(image)
    }
}