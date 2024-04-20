package org.example.project.domain.uc

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.datetime.Clock
import org.example.project.FILE_PROVIDER_AUTHORITY
import org.example.project.imageRootDirectory
import java.io.File

internal class CreateFileForCamera(
    private val context: Context
) {
    operator fun invoke(): Uri {
        val imageFile = File(
            context.imageRootDirectory,
            "${Clock.System.now().toEpochMilliseconds()}.jpg"
        )
        imageFile.createNewFile()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}$FILE_PROVIDER_AUTHORITY",
            imageFile
        )
    }
}