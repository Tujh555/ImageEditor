package org.example.project.domain.compressor

import android.net.Uri
import org.example.project.domain.CompressFormat
import org.example.project.domain.Resolution

interface ImageCompressor {
    fun compress(
        original: Uri,
        saveToFolder: String,
        fileName: String,
        targetResolution: Resolution = Resolution.FULL_HD,
        format: CompressFormat? = CompressFormat.Webp()
    ): Uri?
}