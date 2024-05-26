package org.example.project.domain.uc

import android.net.Uri
import implementation.domain.models.Image
import implementation.domain.models.Resource
import implementation.domain.models.flatMap
import implementation.domain.models.map
import implementation.domain.models.onSuccess
import implementation.domain.repository.ImageRepository
import kotlinx.datetime.Clock
import org.example.project.domain.compressor.ImageCompressor
import org.example.project.domain.metadata.MetadataLoader
import java.util.UUID

internal class CompressImage(
    private val repository: ImageRepository,
    private val metadataLoader: MetadataLoader,
    private val imageCompressor: ImageCompressor
) {
    operator fun invoke(uri: Uri): Resource<Any> = metadataLoader
        .load(uri)
        .flatMap { imageMetadata ->
            imageCompressor.compress(
                original = uri,
                fileName = imageMetadata.name,
            )
        }
        .flatMap { compressedUri ->
            metadataLoader
                .load(compressedUri)
                .map { it to compressedUri.toString() }
        }
        .onSuccess { (compressedMetadata, path) ->
            val image = Image(
                id = UUID.randomUUID().toString(),
                name = compressedMetadata.name,
                saveDate = Clock.System.now(),
                path = path
            )

            repository.insert(image)
        }
}