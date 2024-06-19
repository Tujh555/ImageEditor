package org.example.project.domain.uc

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import implementation.domain.repository.ImageRepository
import org.example.project.imageRootDirectory
import java.io.File

internal class DeleteImage(
    private val context: Context,
    private val repository: ImageRepository
) {
    operator fun invoke(id: String, uri: Uri): Result<Unit> = runCatching {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.delete(uri, null, null)
        } else {
            context.imageRootDirectory.listFiles()?.forEach { file ->
                if (file.path == uri.path) {
                    file.delete()
                    return@forEach
                }
            }
        }

        repository.delete(id)
    }
}