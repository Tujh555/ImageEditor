package org.example.project.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import implementation.domain.models.Resource
import implementation.domain.models.toSuccessResource
import kotlinx.datetime.Clock
import org.example.project.domain.metadata.ImageMetadata
import org.example.project.domain.metadata.MetadataLoader
import java.io.File

internal class MetadataLoaderImpl(private val context: Context) : MetadataLoader {
    private val randomName: String
        get() = Clock.System.now().toEpochMilliseconds().toString()

    override fun load(uri: Uri): Resource<ImageMetadata> {
        val metadata = uri.select(
            asFile = {
                ImageMetadata(
                    name = name,
                    size = calculateSize()
                )
            },
            asContent = {
                val queryResult = runCatching {
                    val name: String
                    val size: Long

                    val cursor = query(uri, null, null, null, null)

                    if (cursor != null) {
                        cursor.use {
                            with(it) {
                                moveToFirst()
                                val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                val sizeIndex = getColumnIndex(OpenableColumns.SIZE)

                                name = getStringOrNull(nameIndex) ?: randomName
                                size = getLongOrNull(sizeIndex) ?: 0L
                            }
                        }
                    } else {
                        name = randomName
                        size = 0L
                    }

                    ImageMetadata(
                        name = name,
                        size = size
                    )
                }

                queryResult.getOrNull()
            }
        )

        return metadata
            ?.toSuccessResource()
            ?: Resource.Failure()
    }

    private inline fun <R> Uri.select(
        asFile: File.() -> R,
        asContent: ContentResolver.() -> R
    ): R? {
        return when (scheme) {
            ContentResolver.SCHEME_CONTENT -> context.contentResolver?.asContent()

            else -> path?.let { File(it) }?.asFile()
        }
    }

    private fun File.calculateSize(): Long = walkTopDown()
        .fold(0L) { acc, file ->
            acc + if (file.isDirectory) {
                0L
            } else {
                file.length()
            }
        }
}