package org.example.project.presentation.models

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import implementation.domain.models.Image
import kotlinx.datetime.Instant

@Immutable
internal data class ImageUiModel(
    val id: String,
    val name: String,
    val saveDate: String,
    val path: Uri
)

internal inline fun Image.toUi(formatDate: (Instant) -> String) = ImageUiModel(
    id = id,
    name = name,
    saveDate = formatDate(saveDate),
    path = path.toUri()
)