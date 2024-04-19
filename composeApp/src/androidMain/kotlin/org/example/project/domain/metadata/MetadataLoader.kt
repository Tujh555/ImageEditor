package org.example.project.domain.metadata

import android.net.Uri
import implementation.domain.models.Resource

internal interface MetadataLoader {
    fun load(uri: Uri): Resource<ImageMetadata>
}