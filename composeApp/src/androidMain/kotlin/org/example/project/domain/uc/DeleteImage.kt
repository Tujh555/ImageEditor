package org.example.project.domain.uc

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import implementation.domain.repository.ImageRepository
import java.io.File

internal class DeleteImage(
    private val context: Context,
    private val repository: ImageRepository
) {
    operator fun invoke(id: String, uri: Uri): Result<Unit> = runCatching {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.delete(uri, null, null)
        } else {
            uri.toFile().delete()
        }

        repository.delete(id)
    }
}