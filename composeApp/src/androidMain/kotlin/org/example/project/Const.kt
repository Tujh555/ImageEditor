package org.example.project

import android.content.Context
import java.io.File

internal const val FILE_PROVIDER_AUTHORITY = ".fileProviderAuthority"
internal const val ROOT_DIRECTORY_PATH = "images/"

internal val Context.imageRootDirectory: File
    get() = File(filesDir.absolutePath + File.separator + ROOT_DIRECTORY_PATH)
        .apply {
            if (exists().not()) {
                mkdirs()
            }
        }

internal fun File.child(name: String) = File(this, name).apply { createNewFile() }