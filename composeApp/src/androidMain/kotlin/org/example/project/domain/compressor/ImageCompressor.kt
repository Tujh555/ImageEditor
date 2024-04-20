package org.example.project.domain.compressor

import android.net.Uri
import implementation.domain.models.Resource

internal interface ImageCompressor {
    fun compress(
        original: Uri,
        fileName: String,
        targetResolution: Resolution = Resolution.FULL_HD,
        format: CompressFormat? = CompressFormat.Jpeg()
    ): Resource<Uri>
}